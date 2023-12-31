package com.github.danielm94.server.handlers.book.httpmethod;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.danielm94.server.domain.book.serializer.JsonBookSerializer;
import com.github.danielm94.server.services.read.GetAllBooksService;
import com.github.danielm94.server.services.read.GetBookByIdService;
import com.sun.net.httpserver.HttpExchange;
import lombok.NonNull;
import lombok.val;

import java.util.UUID;

import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;
import static com.github.danielm94.server.exchange.Attributes.BOOK_ID;

public class GetBookHandler implements HttpMethodBookHandler {
    @Override
    public void handle(@NonNull HttpExchange exchange) {
        val mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(WRITE_DATES_AS_TIMESTAMPS);

        val bookSerializer = new JsonBookSerializer(mapper);

        val attributes = exchange.getHttpContext().getAttributes();
        val resourceId = (UUID) attributes.get(BOOK_ID.toString());

        if (resourceId == null) new GetAllBooksService(bookSerializer).get(exchange);
        else {
            attributes.remove(BOOK_ID.toString());
            new GetBookByIdService(bookSerializer, resourceId).get(exchange);
        }
    }
}
