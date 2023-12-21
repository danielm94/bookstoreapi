package org.example.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.val;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class RequestHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        val requestBody = exchange.getRequestBody();
        val data = requestBody == null ? "" : new Scanner(exchange.getRequestBody()).useDelimiter("\\A").next();

        val message = "Hey I got your message! You sent me the following body - " + data;
        val responseBytes = message.getBytes(StandardCharsets.UTF_8);

        exchange.sendResponseHeaders(200, responseBytes.length);
        val body = exchange.getResponseBody();
        body.write(responseBytes);
        body.close();
    }


}
