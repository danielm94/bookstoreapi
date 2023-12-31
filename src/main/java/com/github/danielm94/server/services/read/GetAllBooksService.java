package com.github.danielm94.server.services.read;

import com.github.danielm94.server.domain.book.Book;
import com.github.danielm94.server.domain.book.serializer.BookSerializationException;
import com.github.danielm94.server.domain.book.serializer.BookSerializer;
import com.sun.net.httpserver.HttpExchange;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.flogger.Flogger;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static com.github.danielm94.database.repository.BookRepository.*;
import static com.github.danielm94.server.domain.book.mappers.BookMapper.*;
import static com.github.danielm94.server.handlers.SimpleResponseHandler.sendResponse;
import static com.github.danielm94.server.requestdata.content.ContentType.*;
import static com.github.danielm94.server.requestdata.headers.HttpHeader.*;
import static com.github.danielm94.server.response.ResponseDispatcher.createResponse;
import static java.net.HttpURLConnection.*;

@Flogger
@AllArgsConstructor
public class GetAllBooksService implements GetBookService {
    private final BookSerializer bookSerializer;

    @Override
    public void get(@NonNull HttpExchange exchange) {
        ResultSet resultSet;
        try {
            resultSet = getBooks();
        } catch (SQLException e) {
            sendResponse(exchange, HTTP_INTERNAL_ERROR, "An error occurred while getting books from the database.");
            return;
        } catch (InterruptedException e) {
            sendResponse(exchange, HTTP_INTERNAL_ERROR, "An error occurred while managing connections to the database.");
            return;
        }

        List<Book> books;
        try {
            books = mapFromResultSet(resultSet);
        } catch (SQLException e) {
            sendResponse(exchange, HTTP_INTERNAL_ERROR, "An error occurred while mapping data from the database.");
            return;
        }

        if (books.isEmpty()) {
            sendResponse(exchange, HTTP_NOT_FOUND, "Books table is empty.");
            return;
        }

        String body;
        try {
            body = bookSerializer.serializeBooks(books);
        } catch (BookSerializationException e) {
            sendResponse(exchange, HTTP_INTERNAL_ERROR, "Server failed to create response body.");
            return;
        }

        try {
            createResponse(exchange)
                    .setResponseCode(HTTP_OK)
                    .addHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString())
                    .setBody(body)
                    .sendResponse();
        } catch (IOException e) {
            log.atSevere()
               .withCause(e)
               .log("Failed to send response to server after getting all books.");
        }
    }
}
