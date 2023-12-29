package com.github.danielm94.server.handlers.book.httpmethod;

import com.sun.net.httpserver.HttpExchange;
import lombok.NonNull;

public interface HttpMethodBookHandler {
    void handle(@NonNull HttpExchange exchange);
}
