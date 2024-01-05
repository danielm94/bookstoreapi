package com.github.danielm94.server.handlers.book.httpmethod;

import com.github.danielm94.server.requestdata.content.ContentType;
import com.github.danielm94.server.requestdata.content.UnsupportedContentTypeException;
import com.github.danielm94.server.services.update.PatchBookService;
import com.github.danielm94.server.services.update.factory.PatchBookServiceFactory;
import com.sun.net.httpserver.HttpExchange;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.val;

import static com.github.danielm94.server.exchange.Attributes.BOOK_ID;
import static com.github.danielm94.server.handlers.SimpleResponseHandler.sendResponse;
import static com.github.danielm94.server.requestdata.content.ContentType.getContentTypeFromString;
import static com.github.danielm94.server.requestdata.headers.HttpHeader.CONTENT_TYPE;
import static com.github.danielm94.server.requestdata.method.HttpMethod.PATCH;
import static com.github.danielm94.server.requestdata.validation.RequestDataValidator.hasAttribute;
import static com.github.danielm94.server.requestdata.validation.RequestDataValidator.hasRequestBody;
import static java.lang.String.format;
import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_UNSUPPORTED_TYPE;
import static java.util.UUID.fromString;

@AllArgsConstructor
public class PatchBookHandler implements HttpMethodBookHandler {
    @NonNull
    private final PatchBookServiceFactory factory;


    @Override
    public void handle(@NonNull HttpExchange exchange) {
        if (!hasAttribute(exchange, BOOK_ID)) {
            sendResponse(exchange, HTTP_BAD_REQUEST, "You must specify the UUID of the book in the path.");
            return;
        }

        if (!hasRequestBody(exchange)) {
            sendResponse(exchange, HTTP_BAD_REQUEST, "You must include a body of some type in your request in order to patch a book.");
            return;
        }

        val headers = exchange.getRequestHeaders();
        val contentTypeHeaderValue = headers.getFirst(CONTENT_TYPE.toString());

        ContentType contentType;
        try {
            contentType = getContentTypeFromString(contentTypeHeaderValue);
        } catch (UnsupportedContentTypeException e) {
            sendResponse(exchange, HTTP_BAD_REQUEST, contentTypeHeaderValue + " is not a supported content type.");
            return;
        }

        PatchBookService service;
        try {
            service = factory.getService(contentType);
        } catch (UnsupportedContentTypeException e) {
            val responseMessage = format("Server does not support content type [%s] for http method [%s] at the endpoint [%s].",
                    contentType, PATCH, exchange.getHttpContext().getPath());
            sendResponse(exchange, HTTP_UNSUPPORTED_TYPE, responseMessage);
            return;
        }

        val attributes = exchange.getHttpContext().getAttributes();
        val uuidString = attributes.get(BOOK_ID.toString()).toString();
        val uuid = fromString(uuidString);
        service.patch(exchange, uuid);
    }
}
