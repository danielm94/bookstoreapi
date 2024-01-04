package com.github.danielm94.server.handlers.book.httpmethod;

import com.github.danielm94.server.domain.book.serializer.BookSerializer;
import com.github.danielm94.server.domain.book.serializer.factory.BookSerializerFactory;
import com.github.danielm94.server.requestdata.content.ContentType;
import com.github.danielm94.server.requestdata.content.UnsupportedContentTypeException;
import com.github.danielm94.server.services.read.GetBookService;
import com.sun.net.httpserver.HttpExchange;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.val;

import java.util.UUID;

import static com.github.danielm94.server.exchange.Attributes.BOOK_ID;
import static com.github.danielm94.server.handlers.SimpleResponseHandler.sendResponse;
import static com.github.danielm94.server.requestdata.content.ContentType.getContentTypeFromString;
import static com.github.danielm94.server.requestdata.headers.HttpHeader.ACCEPT;
import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_NOT_ACCEPTABLE;

@AllArgsConstructor
public class GetBookHandler implements HttpMethodBookHandler {
    @NonNull
    private GetBookService bookService;
    @NonNull
    private BookSerializerFactory factory;

    @Override
    public void handle(@NonNull HttpExchange exchange) {
        val attributes = exchange.getHttpContext().getAttributes();
        val resourceId = (UUID) attributes.get(BOOK_ID.toString());

        val headers = exchange.getRequestHeaders();
        val accept = headers.get(ACCEPT.toString()).getFirst();

        ContentType contentType;
        try {
            contentType = getContentTypeFromString(accept);
        } catch (UnsupportedContentTypeException e) {
            sendResponse(exchange, HTTP_BAD_REQUEST, accept + " is not a supported content type.");
            return;
        }
        
        BookSerializer serializer;
        try {
            serializer = factory.getSerializer(contentType);
        } catch (UnsupportedContentTypeException e) {
            sendResponse(exchange, HTTP_NOT_ACCEPTABLE, "Server does not support formatting the books into " + contentType);
            return;
        }

        if (resourceId == null) {
            bookService.getAll(exchange, serializer);
        } else {
            attributes.remove(BOOK_ID.toString());
            bookService.getAllById(exchange, serializer, resourceId);
        }
    }
}
