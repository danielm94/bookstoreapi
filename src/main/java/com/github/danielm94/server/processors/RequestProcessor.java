package com.github.danielm94.server.processors;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.SneakyThrows;

import java.net.Socket;

public class RequestProcessor implements Runnable {
    private final Socket clientSocket;
    private final HttpHandler requestHandler;
    private final HttpExchange exchange;

    public RequestProcessor(Socket clientSocket, HttpExchange exchange) {
        this.clientSocket = clientSocket;
        this.exchange = exchange;
        this.requestHandler = exchange.getHttpContext().getHandler();
    }

    @SneakyThrows
    @Override
    public void run() {
        requestHandler.handle(exchange);
        clientSocket.close();
    }
}
