package com.github.danielm94.server.services.read;

import com.github.danielm94.server.domain.book.Book;
import com.github.danielm94.server.domain.book.serializer.BookSerializationException;
import com.github.danielm94.server.domain.book.serializer.JsonBookSerializer;
import com.github.danielm94.server.handlers.SimpleResponseHandler;
import com.sun.net.httpserver.HttpExchange;
import lombok.NonNull;
import lombok.extern.flogger.Flogger;
import lombok.val;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static com.github.danielm94.database.repository.BookRepository.*;
import static com.github.danielm94.server.domain.book.mappers.BookMapper.*;
import static com.github.danielm94.server.requestdata.content.ContentType.*;
import static com.github.danielm94.server.requestdata.headers.HttpHeader.*;
import static com.github.danielm94.server.response.ResponseDispatcher.createResponse;
import static java.net.HttpURLConnection.*;

@Flogger
public class GetAllBooksService implements GetBookService {
    @Override
    public void get(@NonNull HttpExchange exchange) {
        ResultSet resultSet;
        try {
            resultSet = getBooks();
        } catch (SQLException e) {
            sendResponseToClient(exchange, HTTP_INTERNAL_ERROR, "An error occurred while getting books from the database.");
            return;
        } catch (InterruptedException e) {
            sendResponseToClient(exchange, HTTP_INTERNAL_ERROR, "An error occurred while managing connections to the database.");
            return;
        }

        List<Book> books;
        try {
            books = mapFromResultSet(resultSet);
        } catch (SQLException e) {
            sendResponseToClient(exchange, HTTP_INTERNAL_ERROR, "An error occurred while mapping data from the database.");
            return;
        }

        if (books.isEmpty()) {
            sendResponseToClient(exchange, HTTP_NOT_FOUND, "Books table is empty.");
            return;
        }


        val serializer = new JsonBookSerializer();
        String body;
        try {
            body = serializer.serializeBooks(books);
        } catch (BookSerializationException e) {
            sendResponseToClient(exchange, HTTP_INTERNAL_ERROR, "Server failed to create response body.");
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
