package com.github.danielm94.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.flogger.Flogger;

@Flogger
@AllArgsConstructor
public class ExceptionHandler implements HttpHandler {
    @NonNull
    private final String logMessage;
    @NonNull
    private final Throwable cause;

    @Override
    public void handle(HttpExchange exchange) {
        log.atSevere()
           .withCause(cause)
           .log(logMessage);
    }
}
