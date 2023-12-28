package com.github.danielm94.server.services.create;

import com.github.danielm94.database.repository.BookRepository;
import com.github.danielm94.server.domain.book.BookDTO;
import com.github.danielm94.server.domain.book.mappers.BookMapper;
import com.github.danielm94.server.domain.book.mappers.JsonBookDTOMapper;
import com.github.danielm94.server.handlers.SimpleResponseHandler;
import com.sun.net.httpserver.HttpExchange;
import lombok.NonNull;
import lombok.extern.flogger.Flogger;
import lombok.val;

import java.io.IOException;
import java.sql.SQLException;

import static java.net.HttpURLConnection.*;

@Flogger
public class JsonCreateBookService implements CreateBookService {

    @Override
    public void createBook(@NonNull HttpExchange exchange) {
        val dtoParser = new JsonBookDTOMapper();

        BookDTO bookDTO;
        try {
            bookDTO = dtoParser.parseRequestBodyToBookDTO(exchange.getRequestBody());
        } catch (IOException e) {
            sendResponseToClient(exchange, HTTP_INTERNAL_ERROR, "Server failed to parse request body into a book suitable for creation operations.");
            return;
        }

        val book = BookMapper.mapFromDTO(bookDTO);
        boolean bookWasCreated;
        try {
            bookWasCreated = BookRepository.createBook(book);
        } catch (SQLException e) {
            sendResponseToClient(exchange, HTTP_INTERNAL_ERROR,
                    "Server failed to create a new book in the database as an error occurred while performing database operations.");
            return;
        } catch (InterruptedException e) {
            sendResponseToClient(exchange, HTTP_INTERNAL_ERROR,
                    "Server failed to create a new book in the database as an error occurred when managing database connections.");
            return;
        }

        if (bookWasCreated) {
            sendResponseToClient(exchange, HTTP_CREATED, null);
        } else {
            sendResponseToClient(exchange, HTTP_INTERNAL_ERROR, "Failed to create book resource in database.");
        }
    }

    private void sendResponseToClient(HttpExchange exchange, int responseStatus, String message) {
        try {
            new SimpleResponseHandler(responseStatus, message).handle(exchange);
        } catch (IOException e) {
            log.atSevere()
               .withCause(e)
               .log("Server failed to send response to the client.\nResponse Status: %d\nResponse Message: %s", responseStatus, message);
        }
    }
}
