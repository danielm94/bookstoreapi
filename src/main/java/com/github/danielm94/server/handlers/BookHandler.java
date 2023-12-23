package com.github.danielm94.server.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.danielm94.server.HttpMethod;
import com.github.danielm94.server.domain.Book;
import com.github.danielm94.server.domain.BookDTO;
import com.github.danielm94.server.util.io.IOUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.val;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

public class BookHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        val httpMethod = HttpMethod.getHttpMethodFromStringValue(exchange.getRequestMethod());
        if (httpMethod == HttpMethod.POST) {
            val headers = exchange.getRequestHeaders();
            val contentType = headers.getFirst("Content-Type");
            if (!contentType.equals("application/json")) {
                new FailureHandler(400, "You fucking dumb dumb JSON only lmao").handle(exchange);
            }
            val json = IOUtil.parseInputStreamToText(exchange.getRequestBody());

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
            System.out.println();
        }
    }
}
