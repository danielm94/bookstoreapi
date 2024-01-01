package com.github.danielm94.server.services.update;

import com.github.danielm94.database.repository.BookRepository;
import com.github.danielm94.database.repository.MissingBookIDException;
import com.github.danielm94.server.domain.book.Book;
import com.github.danielm94.server.domain.book.BookDTO;
import com.github.danielm94.server.domain.book.mappers.BookDTOMapper;
import com.sun.net.httpserver.HttpExchange;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.flogger.Flogger;
import lombok.val;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import static com.github.danielm94.database.repository.BookRepository.*;
import static com.github.danielm94.server.domain.book.mappers.BookMapper.*;
import static com.github.danielm94.server.handlers.SimpleResponseHandler.sendResponse;
import static java.net.HttpURLConnection.*;
import static java.time.LocalDateTime.now;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;

@Flogger
@RequiredArgsConstructor
public class JsonPutBookService implements PutBookService {
    @NonNull
    private final BookDTOMapper dtoMapper;

    private void beginUpdatingTheBook(@NonNull HttpExchange exchange, @NonNull Book oldBook) {
        BookDTO bookDTO;
        try {
            bookDTO = dtoMapper.parseRequestBodyToBookDTO(exchange.getRequestBody());
        } catch (IOException e) {
            sendResponse(exchange, HTTP_INTERNAL_ERROR, "Server failed to parse request body into a book.");
            return;
        }

        if (oldBook.isEqualToDTO(bookDTO)) {
            sendResponse(exchange, HTTP_OK, "Book was not updated as the existing data is identical.");
            return;
        }

        val newBook = Book.builder()
                          .id(oldBook.getId())
                          .bookName(bookDTO.getBookName())
                          .author(bookDTO.getAuthor())
                          .isbn(bookDTO.getIsbn())
                          .price(bookDTO.getPrice())
                          .dateAdded(oldBook.getDateAdded())
                          .dateUpdated(now())
                          .build();

        int rowsUpdated;
        try {
            rowsUpdated = BookRepository.updateBook(newBook);
        } catch (MissingBookIDException e) {
            sendResponse(exchange, HTTP_INTERNAL_ERROR, "Server fumbled the ID somewhere along the lines when it came time to update the book.");
            return;
        } catch (SQLException e) {
            sendResponse(exchange, HTTP_INTERNAL_ERROR, "An error occurred while performing database operations.");
            return;
        } catch (InterruptedException e) {
            sendResponse(exchange, HTTP_INTERNAL_ERROR, "An error occurred while managing database connections.");
            return;
        }

        val bookWasUpdated = rowsUpdated == 1;
        if (bookWasUpdated) {
            sendResponse(exchange, HTTP_NO_CONTENT, null);
        } else {
            sendResponse(exchange, HTTP_OK, "No books were updated.");
        }
    }

    @Override
    public void updateBook(@NonNull HttpExchange exchange, @NonNull UUID uuid) {
        ResultSet resultSet;
        try {
            resultSet = getBook(uuid);
        } catch (SQLException e) {
            sendResponse(exchange, HTTP_INTERNAL_ERROR, "An error occurred while performing database operations.");
            return;
        } catch (InterruptedException e) {
            sendResponse(exchange, HTTP_INTERNAL_ERROR, "An error occurred while managing database connections.");
            return;
        }

        List<Book> books;
        try {
            books = mapFromResultSet(resultSet, ISO_LOCAL_DATE_TIME);
        } catch (SQLException e) {
            sendResponse(exchange, HTTP_INTERNAL_ERROR, "An error occurred while mapping data from the database.");
            return;
        }

        if (books.size() > 1) {
            sendResponse(exchange, HTTP_INTERNAL_ERROR, "Server found more than one entry matching the id: " + uuid);
        } else if (books.isEmpty()) {
            sendResponse(exchange, HTTP_NOT_FOUND, "There are no books with a UUID of: " + uuid);
        } else {
            beginUpdatingTheBook(exchange, books.getFirst());
        }
    }
}
