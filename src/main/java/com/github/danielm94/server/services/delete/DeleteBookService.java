package com.github.danielm94.server.services.delete;

import com.sun.net.httpserver.HttpExchange;
import lombok.NonNull;

import java.util.UUID;

public interface DeleteBookService {
    void delete(@NonNull HttpExchange exchange, @NonNull UUID id);
}
