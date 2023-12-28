package com.github.danielm94.server.handlers;

import com.github.danielm94.server.HttpMethod;
import com.github.danielm94.server.services.create.JsonCreateBookService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.SneakyThrows;
import lombok.val;

public class BookHandler implements HttpHandler {
    @SneakyThrows
    @Override
    public void handle(HttpExchange exchange) {
        val httpMethod = HttpMethod.getHttpMethodFromStringValue(exchange.getRequestMethod());
        if (httpMethod == HttpMethod.POST) {
            val createBookService = new JsonCreateBookService();
            createBookService.createBook(exchange);
        }
    }
}
