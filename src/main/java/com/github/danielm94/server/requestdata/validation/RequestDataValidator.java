package com.github.danielm94.server.requestdata.validation;

import com.github.danielm94.server.exchange.Attributes;
import com.github.danielm94.server.requestdata.headers.HttpHeader;
import com.sun.net.httpserver.HttpExchange;
import lombok.NonNull;
import lombok.extern.flogger.Flogger;
import lombok.val;

import static com.github.danielm94.server.requestdata.headers.HttpHeader.*;

@Flogger
public class RequestDataValidator {
    private RequestDataValidator() {
    }

    public static boolean hasRequestBody(@NonNull HttpExchange exchange) {
        val requestHeaders = exchange.getRequestHeaders();
        if (requestHeaders.containsKey(CONTENT_LENGTH.toString())) {
            try {
                int contentLength = Integer.parseInt(requestHeaders.getFirst(CONTENT_LENGTH.toString()));
                return contentLength > 0;
            } catch (NumberFormatException e) {
                log.atWarning().withCause(e).log("Failed to parse Content-Length header.");
            }
        }
        return false;
    }

    public static boolean hasHeader(@NonNull HttpExchange exchange, @NonNull HttpHeader header) {
        val headers = exchange.getRequestHeaders();
        val headerValue = headers.getFirst(header.toString());
        return headerValue != null;
    }

    public static boolean hasAttribute(@NonNull HttpExchange exchange, @NonNull Attributes attribute) {
        val attributes = exchange.getHttpContext().getAttributes();
        return attributes.get(attribute.toString()) != null;
    }
}
