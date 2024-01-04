package com.github.danielm94.server.services.read;

import com.github.danielm94.server.domain.book.serializer.BookSerializer;
import com.sun.net.httpserver.HttpExchange;
import lombok.NonNull;

import java.util.UUID;

public interface GetBookService {
    void getAll(@NonNull HttpExchange exchange, @NonNull BookSerializer bookSerializer);

    void getAllById(@NonNull HttpExchange exchange, @NonNull BookSerializer bookSerializer, @NonNull UUID uuid);
}
