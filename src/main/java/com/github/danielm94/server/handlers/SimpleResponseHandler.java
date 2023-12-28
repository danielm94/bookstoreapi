package com.github.danielm94.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.AllArgsConstructor;
import lombok.extern.flogger.Flogger;
import lombok.val;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Flogger
@AllArgsConstructor
public class SimpleResponseHandler implements HttpHandler {
    private final int statusCode;
    private final String message;

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        val responseBytes = message.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        try (val body = exchange.getResponseBody()) {
            body.write(responseBytes);
        } catch (IOException e) {
            log.atSevere()
               .withCause(e)
               .log("Failed to write response to the client.",
                       statusCode, message, exchange);
        }
    }
}
