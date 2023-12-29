package com.github.danielm94.server.handlers.book.httpmethod;

import com.github.danielm94.server.services.read.GetAllBooksService;
import com.github.danielm94.server.services.read.GetBookByIdService;
import com.sun.net.httpserver.HttpExchange;
import lombok.NonNull;
import lombok.val;

import java.util.UUID;

import static com.github.danielm94.server.exchange.Attributes.BOOK_ID;

public class GetBookHandler implements HttpMethodBookHandler {
    @Override
    public void handle(@NonNull HttpExchange exchange) {
        val attributes = exchange.getHttpContext().getAttributes();
        val resourceId = (UUID) attributes.get(BOOK_ID.toString());
        if (resourceId == null) new GetAllBooksService().get(exchange);
        else {
            attributes.remove(BOOK_ID.toString());
            new GetBookByIdService(resourceId).get(exchange);
        }
    }
}
