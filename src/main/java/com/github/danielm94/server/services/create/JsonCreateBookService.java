package com.github.danielm94.server.services.create;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.danielm94.ConnectionPoolManager;
import com.github.danielm94.database.schemas.DatabaseSchemas;
import com.github.danielm94.database.schemas.bookstoreapi.DatabaseTables;
import com.github.danielm94.server.domain.book.Book;
import com.github.danielm94.server.domain.book.BookDTO;
import com.github.danielm94.server.handlers.FailureHandler;
import com.github.danielm94.server.requestdata.headers.RequestHeaders;
import com.sun.net.httpserver.HttpExchange;
import lombok.SneakyThrows;
import lombok.val;
import org.apache.commons.io.IOUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.github.danielm94.database.schemas.bookstoreapi.books.BooksColumn.*;
import static java.net.HttpURLConnection.*;

public class JsonCreateBookService implements CreateBookService {
    @SneakyThrows
    @Override
    public void createBook(HttpExchange exchange) {
        val headers = exchange.getRequestHeaders();
        val contentType = headers.getFirst(RequestHeaders.CONTENT_TYPE.toString());
        if (!contentType.equals("application/json")) {
            new FailureHandler(HTTP_BAD_REQUEST, "You fucking dumb dumb JSON only lmao").handle(exchange);
            return;
        }
        val json = IOUtils.toString(exchange.getRequestBody(), StandardCharsets.UTF_8);

        val objectMapper = new ObjectMapper();

        val bookDTO = objectMapper.readValue(json, BookDTO.class);
        val now = LocalDateTime.now();
        val book = Book.builder()
                       .id(UUID.randomUUID())
                       .bookName(bookDTO.getBookName())
                       .author(bookDTO.getBookName())
                       .isbn(bookDTO.getIsbn())
                       .price(bookDTO.getPrice())
                       .dateAdded(now)
                       .dateUpdated(now)
                       .build();
        val connection = ConnectionPoolManager.getInstance().getConnection();

        val statement = connection.createStatement();
        val query = String.format("insert into %s.%s(%s,%s,%s,%s,%s,%s,%s) values('%s','%s','%s','%s','%s','%s','%s')",
                DatabaseSchemas.BOOKSTOREAPI, DatabaseTables.BOOKS,
                ID, BOOK_NAME, AUTHOR, ISBN, PRICE, DATE_ADDED, DATE_UPDATED,
                book.getId(), book.getBookName(), book.getAuthor(), book.getIsbn(), book.getPrice(), book.getDateAdded(), book.getDateUpdated());
        val rowsAffected = statement.executeUpdate(query);
        if (rowsAffected > 0) {
            exchange.sendResponseHeaders(HTTP_CREATED, 0);
        } else {
            new FailureHandler(HTTP_INTERNAL_ERROR, "Failed to create book resource in database.");

        }
        ConnectionPoolManager.getInstance().returnConnection(connection);
    }
}
