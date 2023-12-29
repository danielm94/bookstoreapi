package com.github.danielm94.server.services.update;

import com.sun.net.httpserver.HttpExchange;
import lombok.NonNull;

public interface PatchBookService {

    void patch(@NonNull HttpExchange exchange);
}
