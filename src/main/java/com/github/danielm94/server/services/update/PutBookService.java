package com.github.danielm94.server.services.update;

import com.sun.net.httpserver.HttpExchange;
import lombok.NonNull;

public interface PutBookService {
    void updateBook(@NonNull HttpExchange exchange);
}
