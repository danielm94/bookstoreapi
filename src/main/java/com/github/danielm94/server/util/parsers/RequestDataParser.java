package com.github.danielm94.server.util.parsers;

import com.github.danielm94.server.context.BookContext;
import com.github.danielm94.server.exchange.BookHttpExchange;
import com.github.danielm94.server.handlers.FailureHandler;
import com.github.danielm94.server.util.io.IOUtil;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpHandler;
import lombok.NonNull;
import lombok.extern.flogger.Flogger;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import com.github.danielm94.server.handlers.DoNothingHandler;
import com.github.danielm94.server.util.request.RequestLineParser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.util.Map;

@Flogger
public class RequestDataParser {
    public static final int NO_BODY_IN_REQUEST = -1;
    public static final String NEW_LINE_REGEX_PATTERN = "\r\n|\n";

    private final Map<String, HttpContext> contextMap;

    public RequestDataParser(@NonNull Map<String, HttpContext> contextMap) {
        this.contextMap = contextMap;
    }

    public BookHttpExchange getBookHttpExchangeFromClientSocket(@NonNull Socket clientSocket) {
        log.atFine().log("Processing new client request from socket: %s", clientSocket);

        val exchange = new BookHttpExchange();
        try {
            exchange.setResponseBody(clientSocket.getOutputStream());
        } catch (IOException e) {
            log.atWarning()
               .withCause(e)
               .log("Could not get the output stream from the client socket.");
            setHandler(exchange, new DoNothingHandler());
            return exchange;
        }

        val clientInputStream = getClientInputStream(clientSocket);
        if (clientInputStream == null) {
            log.atWarning().log("Client input stream is null...");
            return sendOffExchangePrematurely(exchange, HttpURLConnection.HTTP_INTERNAL_ERROR,
                    "Server failed to retrieve input stream from client request.");
        }

        val requestString = IOUtil.parseInputStreamToText(clientInputStream);
        if (StringUtils.isBlank(requestString)) {
            log.atWarning().log("Failed to parse request String");
            return sendOffExchangePrematurely(exchange, HttpURLConnection.HTTP_INTERNAL_ERROR,
                    "Server failed to parse input stream from client request.");
        }

        val requestArray = requestString.split(NEW_LINE_REGEX_PATTERN);

        if (requestArray.length == 0) {
            log.atWarning().log("Client request string is empty after splitting by new line: %s", requestString);
            return sendOffExchangePrematurely(exchange, HttpURLConnection.HTTP_BAD_REQUEST,
                    "Empty request string.");
        }

        val requestLineString = requestArray[0];
        log.atFine().log("Parsing request line string [%s] and mapping to an object...", requestLineString);
        val requestLine = RequestLineParser.parseRequestLine(requestLineString);
        if (requestLine == null) {
            log.atWarning().log("Request line is null");
            return sendOffExchangePrematurely(exchange, HttpURLConnection.HTTP_BAD_REQUEST,
                    "Malformed request line: " + requestLineString);
        }
        if (requestLine.getHttpMethod() == null) {
            return sendOffExchangePrematurely(exchange, HttpURLConnection.HTTP_BAD_REQUEST,
                    "Unsupported HttpMethod found in request line: " + requestLineString);
        }

        exchange.setRequestMethod(requestLine.getHttpMethod().toString());

        val path = requestLine.getPath();
        val context = contextMap.get(path);
        if (context == null) {
            log.atWarning().log("Server does not support the path: %s", path);
            return sendOffExchangePrematurely(exchange, HttpURLConnection.HTTP_NOT_FOUND,
                    "Server does not support the path: " + path);
        }
        exchange.setHttpContext(context);

        val protocol = requestLine.getProtocol();
        exchange.setProtocol(protocol);

        val messageBodyLine = parseHeaders(requestArray, exchange);

        val bodyFoundInRequest = messageBodyLine != NO_BODY_IN_REQUEST;
        if (bodyFoundInRequest) {
            parseBody(requestArray, exchange, messageBodyLine + 1);
        }

        log.atFine().log("Successfully parsed request data from client: %s", clientSocket);
        return exchange;
    }


    private static BookHttpExchange sendOffExchangePrematurely(@NonNull BookHttpExchange exchange, int statusCode, @NonNull String message) {
        log.atFine()
           .log("Sending off HttpExchange prematurely. Status code: %d, Message: %s, Exchange: %s", statusCode, message, exchange);

        val failureHandler = new FailureHandler(statusCode, message);
        setHandler(exchange, failureHandler);
        exchange.setProtocol("HTTP/1.1");
        return exchange;
    }

    private static void setHandler(BookHttpExchange exchange, HttpHandler handler) {
        if (exchange.getHttpContext() == null) {
            exchange.setHttpContext(new BookContext());
        }
        exchange.getHttpContext().setHandler(handler);
    }

    private static InputStream getClientInputStream(@NonNull Socket clientSocket) {
        log.atFine().log("Getting input stream from client %s", clientSocket.getRemoteSocketAddress());
        InputStream inputStream;
        try {
            inputStream = clientSocket.getInputStream();
        } catch (IOException e) {
            log.atWarning()
               .withCause(e)
               .log("Could not get input stream from client %s", clientSocket.getRemoteSocketAddress());
            return null;
        }
        return inputStream;
    }


    private int parseHeaders(@NonNull String[] requestArray, @NonNull BookHttpExchange exchange) {
        log.atFine().log("Parsing headers from request data.");
        val headers = new Headers();
        exchange.setRequestHeaders(headers);
        for (var i = 1; i < requestArray.length; i++) {
            var line = requestArray[i];
            var endOfHeadersReached = line.isBlank() || line.isEmpty();
            if (endOfHeadersReached) return i;
            var keyValueArray = requestArray[i].split(":");
            val key = keyValueArray[0];
            val value = keyValueArray[1];
            headers.add(key, value);
            log.atFinest().log("Adding header with key:%s|value:%s", key, value);
        }
        log.atFine().log("Parsed %d headers from request data.", headers.size());
        return NO_BODY_IN_REQUEST;
    }

    private void parseBody(@NonNull String[] requestArray, @NonNull BookHttpExchange exchange, int start) {
        log.atFine().log("Parsing request body from request data.");
        val bodyBuilder = new StringBuilder();
        for (var i = start; i < requestArray.length; i++) {
            bodyBuilder.append(requestArray[i]).append(System.lineSeparator());
        }
        val body = bodyBuilder.toString();
        log.atFine().log("Parsed the following body from the request data - %s", body);
        val bodyStream = new ByteArrayInputStream(body.getBytes());
        exchange.setRequestBody(bodyStream);
    }


}
