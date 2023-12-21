package org.example.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.extern.flogger.Flogger;

@Flogger
public class DoNothingHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) {
        log.atFine()
                .log("Doing nothing because something must have gone horribly wrong.");
    }
}
