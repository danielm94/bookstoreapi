package com.github.danielm94.server.handlers;

import com.github.danielm94.server.response.ResponseDispatcher;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.flogger.Flogger;

import java.io.IOException;

@Flogger
@AllArgsConstructor
public class SimpleResponseHandler implements HttpHandler {
    private final int statusCode;
    private final String body;

    private static void sendResponseInternal(@NonNull HttpExchange exchange, @NonNull Integer statusCode, String body) throws IOException {
        ResponseDispatcher.createResponse(exchange)
                          .setResponseCode(statusCode)
                          .setBody(body)
                          .sendResponse();
    }

    public static void sendResponse(@NonNull HttpExchange exchange, @NonNull Integer statusCode, String body) {
        try {
            sendResponseInternal(exchange, statusCode, body);
        } catch (IOException e) {
            log.atSevere()
               .withCause(e)
               .log("Server failed to send response to the client.\nResponse Status: %d\nResponse Message: %s", statusCode, body);
        }
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        sendResponseInternal(exchange, statusCode, body);
    }
}
