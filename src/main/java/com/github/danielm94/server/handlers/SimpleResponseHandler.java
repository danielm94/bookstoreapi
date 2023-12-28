package com.github.danielm94.server.handlers;

import com.github.danielm94.server.response.ResponseDispatcher;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.AllArgsConstructor;
import lombok.extern.flogger.Flogger;

import java.io.IOException;

@Flogger
@AllArgsConstructor
public class SimpleResponseHandler implements HttpHandler {
    private final int statusCode;
    private final String body;

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        ResponseDispatcher.createResponse(exchange)
                          .setResponseCode(statusCode)
                          .setBody(body)
                          .sendResponse();
    }
}
