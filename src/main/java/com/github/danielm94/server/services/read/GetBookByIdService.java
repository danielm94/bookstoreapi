package com.github.danielm94.server.services.read;

import com.github.danielm94.server.domain.book.Book;
import com.github.danielm94.server.domain.book.serializer.BookSerializationException;
import com.github.danielm94.server.domain.book.serializer.BookSerializer;
import com.sun.net.httpserver.HttpExchange;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.flogger.Flogger;
import lombok.val;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import static com.github.danielm94.database.repository.BookRepository.*;
import static com.github.danielm94.database.schemas.DatabaseSchemas.*;
import static com.github.danielm94.database.schemas.bookstoreapi.DatabaseTables.*;
import static com.github.danielm94.server.domain.book.mappers.BookMapper.*;
import static com.github.danielm94.server.handlers.SimpleResponseHandler.sendResponse;
import static com.github.danielm94.server.requestdata.content.ContentType.*;
import static com.github.danielm94.server.requestdata.headers.HttpHeader.*;
import static com.github.danielm94.server.response.ResponseDispatcher.createResponse;
import static java.net.HttpURLConnection.*;

@Flogger
@AllArgsConstructor
public class GetBookByIdService implements GetBookService {
    private final BookSerializer bookSerializer;
    private final UUID uuid;

    @Override
    public void get(@NonNull HttpExchange exchange) {
        ResultSet resultSet;
        try {
            resultSet = getBook(uuid);
        } catch (SQLException e) {
            sendResponse(exchange, HTTP_INTERNAL_ERROR, "An error occurred while getting a book from the database.");
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

        if (books.size() > 1) {
            val exceptionMessage = String.format("Duplicate entries detected in the %s.%s table.", BOOKSTOREAPI, BOOKS);
            throw new IllegalStateException(exceptionMessage);
        } else if (books.isEmpty()) {
            sendResponse(exchange, HTTP_NOT_FOUND, "There are no books with a UUID of: " + uuid);
            return;
        }

        val book = books.getFirst();
        String body;
        try {
            body = bookSerializer.serializeBook(book);
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
               .log("Failed to send response to server after getting book by id.");
        }
    }
}
