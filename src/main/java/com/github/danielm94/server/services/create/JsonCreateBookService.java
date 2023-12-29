package com.github.danielm94.server.services.create;

import com.github.danielm94.database.repository.BookRepository;
import com.github.danielm94.server.domain.book.BookDTO;
import com.github.danielm94.server.domain.book.mappers.JsonBookDTOMapper;
import com.github.danielm94.server.requestdata.headers.HttpHeader;
import com.sun.net.httpserver.HttpExchange;
import lombok.NonNull;
import lombok.extern.flogger.Flogger;
import lombok.val;

import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;

import static com.github.danielm94.server.domain.book.mappers.BookMapper.*;
import static com.github.danielm94.server.handlers.SimpleResponseHandler.sendResponse;
import static com.github.danielm94.server.response.ResponseDispatcher.createResponse;
import static java.net.HttpURLConnection.*;

@Flogger
public class JsonCreateBookService implements CreateBookService {

    private static String getLocation(HttpExchange exchange, UUID id) {
        var path = exchange.getHttpContext().getPath();
        path = path.charAt(path.length() - 1) == '/' ? path : path + '/';
        return path + id;
    }

    @Override
    public void createBook(@NonNull HttpExchange exchange) {
        val dtoMapper = new JsonBookDTOMapper();

        BookDTO bookDTO;
        try {
            bookDTO = dtoMapper.parseRequestBodyToBookDTO(exchange.getRequestBody());
        } catch (IOException e) {
            sendResponse(exchange, HTTP_INTERNAL_ERROR, "Server failed to parse request body into a book suitable for creation operations.");
            return;
        }

        val book = mapFromDTO(bookDTO);
        boolean bookWasCreated;
        try {
            val rowsAffected = BookRepository.createBook(book);
            bookWasCreated = rowsAffected > 0;
        } catch (SQLException e) {
            sendResponse(exchange, HTTP_INTERNAL_ERROR,
                    "Server failed to create a new book in the database as an error occurred while performing database operations.");
            return;
        } catch (InterruptedException e) {
            sendResponse(exchange, HTTP_INTERNAL_ERROR,
                    "Server failed to create a new book in the database as an error occurred when managing database connections.");
            return;
        }

        if (bookWasCreated) {
            try {
                createResponse(exchange)
                        .setResponseCode(HTTP_CREATED)
                        .addHeader(HttpHeader.LOCATION.toString(), getLocation(exchange, book.getId()))
                        .sendResponse();
            } catch (IOException e) {
                log.atWarning()
                   .withCause(e)
                   .log("Failed to send response to client after successfully creating a book.");
            }
        } else {
            sendResponse(exchange, HTTP_INTERNAL_ERROR, "Failed to create book resource in database.");
        }
    }
}