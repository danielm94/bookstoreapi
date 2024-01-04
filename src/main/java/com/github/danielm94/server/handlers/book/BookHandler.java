package com.github.danielm94.server.handlers.book;

import com.github.danielm94.server.handlers.book.httpmethod.HttpMethodBookHandler;
import com.github.danielm94.server.handlers.book.httpmethod.factory.HttpMethodBookHandlerFactory;
import com.github.danielm94.server.requestdata.method.UnsupportedHttpMethodException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.flogger.Flogger;
import lombok.val;

import static com.github.danielm94.server.handlers.SimpleResponseHandler.sendResponse;
import static com.github.danielm94.server.requestdata.method.HttpMethod.getHttpMethodFromStringValue;
import static java.net.HttpURLConnection.HTTP_BAD_METHOD;

@Flogger
@AllArgsConstructor
public class BookHandler implements HttpHandler {
    @NonNull
    private final HttpMethodBookHandlerFactory factory;

    @Override
    public void handle(@NonNull HttpExchange exchange) {
        val httpMethod = getHttpMethodFromStringValue(exchange.getRequestMethod());
        HttpMethodBookHandler handler;
        try {
            handler = factory.getHandler(httpMethod);
        } catch (UnsupportedHttpMethodException e) {
            val message = String.format("Server does not support the request method %s for this endpoint.", httpMethod);
            sendResponse(exchange, HTTP_BAD_METHOD, message);
            return;
        }
        handler.handle(exchange);
    }
}
