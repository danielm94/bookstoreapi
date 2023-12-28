package com.github.danielm94.server.handlers;

import com.github.danielm94.server.response.ResponseDispatcher;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.val;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Scanner;

public class RequestHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        val requestBody = exchange.getRequestBody();
        val data = requestBody == null ? "" : new Scanner(exchange.getRequestBody()).useDelimiter("\\A").next();

        val message = "Hey I got your message! You sent me the following body - " + data;

        ResponseDispatcher.createResponse(exchange)
                          .setResponseCode(HttpURLConnection.HTTP_OK)
                          .setBody(message)
                          .sendResponse();
    }
}
