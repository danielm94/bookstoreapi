package com.github.danielm94.server.processors;

import com.github.danielm94.server.parsers.RequestDataParser;
import com.github.danielm94.server.parsers.body.DefaultBodyParserStrategy;
import com.github.danielm94.server.parsers.clientinput.DefaultClientInputParserStrategy;
import com.github.danielm94.server.parsers.headers.DefaultHeaderParserStrategy;
import com.github.danielm94.server.parsers.requestline.DefaultRequestLineParserStrategy;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.flogger.Flogger;
import lombok.val;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;

@Flogger
@AllArgsConstructor
public class RequestProcessor implements Runnable {
    @NonNull
    private final Socket clientSocket;
    @NonNull
    private final Map<String, HttpContext> contextMap;
    @NonNull
    private final Map<Pattern, Function<Map<String, String>, HttpContext>> dynamicPaths;

    private static void handleRequest(HttpHandler requestHandler, HttpExchange exchange) {
        try {
            requestHandler.handle(exchange);
        } catch (IOException e) {
            log.atWarning()
               .withCause(e)
               .log("Failed to handle the request.");
        }
    }

    @Override
    public void run() {
        val parser = new RequestDataParser(contextMap, dynamicPaths,
                new DefaultClientInputParserStrategy(),
                new DefaultRequestLineParserStrategy(),
                new DefaultHeaderParserStrategy(),
                new DefaultBodyParserStrategy());
        val exchange = parser.getHttpExchangeFromClientSocket(clientSocket);
        val requestHandler = exchange.getHttpContext().getHandler();
        handleRequest(requestHandler, exchange);
        closeTheClientSocket();
    }

    private void closeTheClientSocket() {
        try {
            clientSocket.close();
        } catch (IOException e) {
            log.atWarning()
               .withCause(e)
               .log("Failed to close client socket.");
        }
    }
}
