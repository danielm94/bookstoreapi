package com.github.danielm94.server.response;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import lombok.NonNull;
import lombok.val;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class ResponseDispatcher {
    private static final Charset CHARACTER_SET = StandardCharsets.UTF_8;
    private static final int NO_BODY_LENGTH = -1;
    private final Headers headers;
    private final HttpExchange exchange;
    private Integer responseCode;
    private String body;

    private ResponseDispatcher(HttpExchange exchange) {
        this.exchange = exchange;
        this.headers = exchange.getResponseHeaders();
    }

    private static void validateHeader(String key, String... values) {
        if (isBlank(key)) throw new IllegalArgumentException("Key cannot be null, empty, or blank.");
        for (val value : values) {
            if (isBlank(value)) throw new IllegalArgumentException("Value cannot be null, empty, or blank.");
        }
    }

    public static ResponseDispatcher createResponse(@NonNull HttpExchange exchange) {
        return new ResponseDispatcher(exchange);
    }

    public ResponseDispatcher setResponseCode(@NonNull Integer responseCode) {
        this.responseCode = responseCode;
        return this;
    }

    public ResponseDispatcher setBody(@NonNull String body) {
        this.body = body;
        return this;
    }

    public ResponseDispatcher addHeader(@NonNull String key, @NonNull String value) {
        validateHeader(key, value);
        headers.add(key, value);
        return this;
    }

    public ResponseDispatcher addHeader(@NonNull String key, @NonNull List<String> values) {
        validateHeader(key, values.toArray(new String[0]));
        headers.put(key, values);
        return this;
    }

    public void sendResponse() throws IOException {
        if (responseCode == null) {
            throw new IllegalStateException("You must set a response code before attempting to send a response.");
        }

        val bodyByteArray = body == null ? new byte[0] : body.getBytes(CHARACTER_SET);
        val bodyLength = body == null ? NO_BODY_LENGTH : bodyByteArray.length;

        exchange.sendResponseHeaders(responseCode, bodyLength);

        if (body != null) {
            try (val responseBodyOutputStream = exchange.getResponseBody()) {
                responseBodyOutputStream.write(bodyByteArray);
            }
        }
    }
}
