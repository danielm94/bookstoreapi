package com.github.danielm94.server.services.update;

import com.github.danielm94.database.repository.MissingBookIDException;
import com.github.danielm94.server.domain.book.Book;
import com.github.danielm94.server.domain.book.BookDTO;
import com.github.danielm94.server.domain.book.mappers.BookDTOMapper;
import com.sun.net.httpserver.HttpExchange;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;

import static com.github.danielm94.database.repository.BookRepository.*;
import static com.github.danielm94.server.handlers.SimpleResponseHandler.sendResponse;
import static java.net.HttpURLConnection.*;
import static java.time.LocalDateTime.now;

@RequiredArgsConstructor
public class JsonPatchBookService implements PatchBookService {
    @NonNull
    private final BookDTOMapper bookDTOMapper;

    @Override
    public void patch(@NonNull HttpExchange exchange, @NonNull UUID uuid) {
        val body = exchange.getRequestBody();
        BookDTO bookDTO;
        try {
            bookDTO = bookDTOMapper.parseRequestBodyToBookDTO(body);
        } catch (IOException e) {
            sendResponse(exchange, HTTP_INTERNAL_ERROR, "Server failed to parse request body into a book suitable for PATCH operations.");
            return;
        }

        if (bookDTO.allFieldsAreNull()) {
            sendResponse(exchange, HTTP_BAD_REQUEST, "The request body contained no suitable book properties to perform a PATCH operation.");
            return;

        }
        //TODO: Server should send 404 response if requested book to patch doesn't exist.
        val book = Book.builder()
                       .id(uuid)
                       .bookName(bookDTO.getBookName())
                       .author(bookDTO.getAuthor())
                       .isbn(bookDTO.getIsbn())
                       .price(bookDTO.getPrice())
                       .dateUpdated(now())
                       .build();

        int rowsUpdated;
        try {
            rowsUpdated = patchBook(book);
        } catch (SQLException e) {
            sendResponse(exchange, HTTP_INTERNAL_ERROR, "An error occurred while performing database operations.");
            return;
        } catch (InterruptedException e) {
            sendResponse(exchange, HTTP_INTERNAL_ERROR, "An error occurred while managing database connections.");
            return;
        } catch (MissingBookIDException e) {
            sendResponse(exchange, HTTP_INTERNAL_ERROR, "Server fumbled the book ID somewhere along the lines when it came time to update the book.");
            return;
        }

        val bookWasUpdated = rowsUpdated == 1;
        if (bookWasUpdated) {
            sendResponse(exchange, HTTP_NO_CONTENT, null);
        } else {
            sendResponse(exchange, HTTP_OK, "No books were patched.");
        }
    }
}
