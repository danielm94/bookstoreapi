package com.github.danielm94.server.handlers.book.httpmethod;

import com.github.danielm94.server.services.delete.DeleteBookService;
import com.sun.net.httpserver.HttpExchange;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.val;

import java.util.UUID;

import static com.github.danielm94.server.exchange.Attributes.BOOK_ID;
import static com.github.danielm94.server.handlers.SimpleResponseHandler.sendResponse;
import static com.github.danielm94.server.requestdata.validation.RequestDataValidator.hasAttribute;
import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;

@AllArgsConstructor
public class DeleteBookHandler implements HttpMethodBookHandler {
    @NonNull
    private DeleteBookService deleteService;

    @Override
    public void handle(@NonNull HttpExchange exchange) {
        val attributes = exchange.getHttpContext().getAttributes();
        if (!hasAttribute(exchange, BOOK_ID)) {
            sendResponse(exchange, HTTP_BAD_REQUEST, "You must specify the UUID of the book you wish to delete in the URI.");
            return;
        }

        val resourceId = (UUID) attributes.get(BOOK_ID.toString());
        deleteService.delete(exchange, resourceId);
    }
}
