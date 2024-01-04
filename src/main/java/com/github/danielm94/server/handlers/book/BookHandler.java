package com.github.danielm94.server.handlers.book;

import com.github.danielm94.server.handlers.book.httpmethod.HttpMethodBookHandler;
import com.github.danielm94.server.handlers.book.httpmethod.factory.HttpMethodBookHandlerFactory;
import com.github.danielm94.server.requestdata.method.HttpMethod;
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
    public static final int UNSUPPORTED_HTTP_METHOD_STATUS_CODE = HTTP_BAD_METHOD;
    @NonNull
    private final HttpMethodBookHandlerFactory factory;

    public static String getUnsupportedHTTPMethodMessage(HttpMethod httpMethod) {
        return String.format("Server does not support the request method %s for this endpoint.", httpMethod);
    }

    @Override
    public void handle(@NonNull HttpExchange exchange) {
        val httpMethod = getHttpMethodFromStringValue(exchange.getRequestMethod());
        HttpMethodBookHandler handler;
        try {
            handler = factory.getHandler(httpMethod);
        } catch (UnsupportedHttpMethodException e) {
            sendResponse(exchange, UNSUPPORTED_HTTP_METHOD_STATUS_CODE, getUnsupportedHTTPMethodMessage(httpMethod));
            return;
        }
        handler.handle(exchange);
    }
}
