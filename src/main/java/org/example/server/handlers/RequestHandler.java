package org.example.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.Scanner;

public class RequestHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        var inputStream = exchange.getRequestBody();
        var scanner = new Scanner(inputStream);
        var data = scanner.nextLine();
        System.out.println(data);
        var message = "Hey I got your message! You sent me the following body - " + data;
        exchange.sendResponseHeaders(200, message.length());
        var body = exchange.getResponseBody();
        body.write(message.getBytes());
        body.close();
    }

}
