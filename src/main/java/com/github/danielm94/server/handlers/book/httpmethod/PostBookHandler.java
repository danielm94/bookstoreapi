package com.github.danielm94.server.handlers.book.httpmethod;

import com.github.danielm94.server.requestdata.content.ContentType;
import com.github.danielm94.server.requestdata.content.UnsupportedContentTypeException;
import com.github.danielm94.server.services.create.CreateBookService;
import com.github.danielm94.server.services.create.factory.CreateBookServiceFactory;
import com.sun.net.httpserver.HttpExchange;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.val;

import static com.github.danielm94.server.handlers.SimpleResponseHandler.sendResponse;
import static com.github.danielm94.server.requestdata.content.ContentType.getContentTypeFromString;
import static com.github.danielm94.server.requestdata.headers.HttpHeader.CONTENT_TYPE;
import static com.github.danielm94.server.requestdata.method.HttpMethod.POST;
import static com.github.danielm94.server.requestdata.validation.RequestDataValidator.hasHeader;
import static com.github.danielm94.server.requestdata.validation.RequestDataValidator.hasRequestBody;
import static java.lang.String.format;
import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_UNSUPPORTED_TYPE;

@AllArgsConstructor
public class PostBookHandler implements HttpMethodBookHandler {
    @NonNull
    private final CreateBookServiceFactory serviceFactory;

    @Override
    public void handle(@NonNull HttpExchange exchange) {
        if (!hasRequestBody(exchange)) {
            sendResponse(exchange, HTTP_BAD_REQUEST, "You must include a body in the request.");
            return;
        }
        if (!hasHeader(exchange, CONTENT_TYPE)) {
            sendResponse(exchange, HTTP_BAD_REQUEST, "You must include a Content-Type header in your request.");
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

        CreateBookService createBookService;
        try {
            createBookService = serviceFactory.getService(contentType);
        } catch (UnsupportedContentTypeException e) {
            val responseMessage = format("Server does not support content type [%s] for http method [%s] at the endpoint [%s].",
                    contentType, POST, exchange.getHttpContext().getPath());
            sendResponse(exchange, HTTP_UNSUPPORTED_TYPE, responseMessage);
            return;
        }
        createBookService.createBook(exchange);
    }
}
