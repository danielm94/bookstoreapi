package com.github.danielm94.server.services.update;

import com.sun.net.httpserver.HttpExchange;
import lombok.NonNull;

import java.util.UUID;

public interface PatchBookService {

    void patch(@NonNull HttpExchange exchange, @NonNull UUID uuid);
}
