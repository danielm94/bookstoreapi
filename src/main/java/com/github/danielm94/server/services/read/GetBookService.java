package com.github.danielm94.server.services.read;

import com.sun.net.httpserver.HttpExchange;
import lombok.NonNull;

public interface GetBookService {
    void getBook(@NonNull HttpExchange exchange);
}
