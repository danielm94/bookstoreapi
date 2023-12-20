package org.example.server.socket;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.val;
import org.example.server.HttpMethod;
import org.example.server.exchange.BookHttpExchange;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


public class ClientSocketParser {
    public static final int NO_BODY_IN_REQUEST = -1;
    private final Map<String, HttpContext> contextMap;

    public ClientSocketParser(@NonNull Map<String, HttpContext> contextMap) {
        this.contextMap = contextMap;
    }

    @SneakyThrows(IOException.class)
    public BookHttpExchange getBookHttpExchangeFromClientSocket(@NonNull Socket clientSocket) {
        val inputStream = clientSocket.getInputStream();
        val scanner = new Scanner(inputStream);
        val requestList = new ArrayList<String>();
        while (scanner.hasNext()) {
            requestList.add(scanner.nextLine());
        }

        val exchange = new BookHttpExchange();
        parseRequestLine(requestList, exchange);

        val messageBodyLine = parseHeaders(requestList, exchange);

        if (messageBodyLine != NO_BODY_IN_REQUEST) {
            parseBody(requestList, exchange, messageBodyLine + 1);
        }
        return exchange;
    }

    private void parseBody(@NonNull ArrayList<String> requestList, @NonNull BookHttpExchange exchange, int start) {
        val bodyBuilder = new StringBuilder();
        for (var i = start; i < requestList.size(); i++) {
            bodyBuilder.append(requestList.get(i)).append(System.lineSeparator());
        }
        val body = bodyBuilder.toString();
        val bodyStream = new ByteArrayInputStream(body.getBytes());
        exchange.setRequestBody(bodyStream);
    }

    private void parseRequestLine(@NonNull List<String> requestList, @NonNull BookHttpExchange exchange) {
        val requestLine = requestList.getFirst();
        val requestLineValues = requestLine.split("\\s");
        if (requestLineValues.length < 3) exchange.setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST);
        val requestMethod = requestLineValues[0];
        validateRequestMethod(exchange, requestMethod);
        exchange.setRequestMethod(requestMethod);
        val path = requestLineValues[1];
        val context = contextMap.get(path);
        if (context == null) exchange.setResponseCode(HttpURLConnection.HTTP_NOT_FOUND);
        exchange.setHttpContext(context);
        val protocol = requestLineValues[2];
        exchange.setProtocol(protocol);
    }

    private int parseHeaders(@NonNull List<String> requestList, @NonNull BookHttpExchange exchange) {
        val headers = new Headers();
        exchange.setRequestHeaders(headers);
        for (var i = 1; i < requestList.size(); i++) {
            var line = requestList.get(i);
            var endOfHeadersReached = line.isBlank() || line.isEmpty();
            if (endOfHeadersReached) return i;
            var keyValueArray = requestList.get(i).split(":");
            headers.add(keyValueArray[0], keyValueArray[1]);
        }

        return NO_BODY_IN_REQUEST;
    }

    private static void validateRequestMethod(@NonNull BookHttpExchange exchange, @NonNull String requestMethod) {
        try {
            HttpMethod.valueOf(requestMethod);
        } catch (IllegalArgumentException e) {
            exchange.setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST);
        }
    }
}
