package com.github.danielm94.server.services.create;

import com.sun.net.httpserver.HttpExchange;
import lombok.NonNull;

public interface CreateBookService {

    void createBook(@NonNull HttpExchange exchange);
}
