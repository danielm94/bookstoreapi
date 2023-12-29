package com.github.danielm94.server.handlers.book;

import com.github.danielm94.server.handlers.book.httpmethod.HttpMethodBookHandler;
import com.github.danielm94.server.handlers.book.httpmethod.factory.DefaultHttpMethodBookHandlerFactory;
import com.github.danielm94.server.requestdata.method.UnsupportedHttpMethodException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.extern.flogger.Flogger;
import lombok.val;

import static com.github.danielm94.server.handlers.SimpleResponseHandler.sendResponse;
import static com.github.danielm94.server.requestdata.method.HttpMethod.getHttpMethodFromStringValue;
import static java.net.HttpURLConnection.*;

@Flogger
public class BookHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) {
        val httpMethod = getHttpMethodFromStringValue(exchange.getRequestMethod());
        val factory = new DefaultHttpMethodBookHandlerFactory();
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
