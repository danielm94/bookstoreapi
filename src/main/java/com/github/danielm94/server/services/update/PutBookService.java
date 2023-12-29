package com.github.danielm94.server.services.update;

import com.sun.net.httpserver.HttpExchange;
import lombok.NonNull;

import java.util.UUID;

public interface PutBookService {
    void updateBook(@NonNull HttpExchange exchange, @NonNull UUID uuid);
}
