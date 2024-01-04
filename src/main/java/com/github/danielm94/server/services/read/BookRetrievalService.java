package com.github.danielm94.server.services.read;

import com.github.danielm94.server.domain.book.Book;
import com.github.danielm94.server.domain.book.serializer.BookSerializationException;
import com.github.danielm94.server.domain.book.serializer.BookSerializer;
import com.sun.net.httpserver.HttpExchange;
import lombok.NonNull;
import lombok.extern.flogger.Flogger;
import lombok.val;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import static com.github.danielm94.database.repository.BookRepository.getBook;
import static com.github.danielm94.database.repository.BookRepository.getBooks;
import static com.github.danielm94.database.schemas.DatabaseSchemas.BOOKSTOREAPI;
import static com.github.danielm94.database.schemas.bookstoreapi.DatabaseTables.BOOKS;
import static com.github.danielm94.server.domain.book.mappers.BookMapper.mapFromResultSet;
import static com.github.danielm94.server.handlers.SimpleResponseHandler.sendResponse;
import static com.github.danielm94.server.requestdata.content.ContentType.APPLICATION_JSON;
import static com.github.danielm94.server.requestdata.headers.HttpHeader.CONTENT_TYPE;
import static com.github.danielm94.server.response.ResponseDispatcher.createResponse;
import static java.net.HttpURLConnection.*;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;


@Flogger
public class BookRetrievalService implements GetBookService {

    @Override
    public void getAll(@NonNull HttpExchange exchange, @NonNull BookSerializer serializer) {
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
            books = mapFromResultSet(resultSet, ISO_LOCAL_DATE_TIME);
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
            body = serializer.serializeBooks(books);
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

    @Override
    public void getById(@NonNull HttpExchange exchange, @NonNull BookSerializer serializer, @NonNull UUID uuid) {
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
            books = mapFromResultSet(resultSet, ISO_LOCAL_DATE_TIME);
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
            body = serializer.serializeBook(book);
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
