package com.github.danielm94.server.services.create;

import com.sun.net.httpserver.HttpExchange;

public interface CreateBookService {

    void createBook(HttpExchange exchange);
}
