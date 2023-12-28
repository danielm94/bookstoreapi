package com.github.danielm94.server.parsers;

import com.github.danielm94.server.context.BookContext;
import com.github.danielm94.server.exchange.BookHttpExchange;
import com.github.danielm94.server.handlers.DoNothingHandler;
import com.github.danielm94.server.handlers.SimpleResponseHandler;
import com.github.danielm94.server.parsers.body.BodyParserStrategy;
import com.github.danielm94.server.parsers.clientinput.ClientInputParserStrategy;
import com.github.danielm94.server.parsers.clientinput.StreamParsingException;
import com.github.danielm94.server.parsers.headers.HeaderParserStrategy;
import com.github.danielm94.server.parsers.requestline.RequestLine;
import com.github.danielm94.server.parsers.requestline.RequestLineParserStrategy;
import com.github.danielm94.server.parsers.requestline.RequestLineParsingException;
import com.github.danielm94.server.requestdata.headers.HttpHeader;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpHandler;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.flogger.Flogger;
import lombok.val;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Flogger
@AllArgsConstructor
public class RequestDataParser {
    public static final String NEW_LINE_REGEX_PATTERN = "\r\n|\n";

    private final Map<String, HttpContext> contextMap;
    private final ClientInputParserStrategy inputParser;
    private final RequestLineParserStrategy requestLineParser;
    private final HeaderParserStrategy headerParser;
    private final BodyParserStrategy bodyParser;


    private static InputStream getClientInputStream(@NonNull Socket clientSocket) throws InputStreamRetrievalException {
        log.atFine().log("Getting input stream from client %s", clientSocket.getRemoteSocketAddress());
        InputStream inputStream;
        try {
            inputStream = clientSocket.getInputStream();
        } catch (IOException e) {
            val message = "Could not get input stream from client " + clientSocket.getRemoteSocketAddress();
            throw new InputStreamRetrievalException(message, e);
        }
        return inputStream;
    }


    private static BookHttpExchange sendOffExchangePrematurely(@NonNull BookHttpExchange exchange, int statusCode, @NonNull String message) {
        log.atFine()
           .log("Sending off HttpExchange prematurely. Status code: %d, Message: %s, Exchange: %s", statusCode, message, exchange);

        val failureHandler = new SimpleResponseHandler(statusCode, message);
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

    public BookHttpExchange getBookHttpExchangeFromClientSocket(@NonNull Socket clientSocket) {
        log.atFine().log("Processing new client request from socket: %s", clientSocket);

        val exchange = new BookHttpExchange();
        try {
            exchange.setResponseBody(clientSocket.getOutputStream());
            log.atFine()
               .log("Successfully set the response body of the exchange to the client socket's output stream.");
        } catch (IOException e) {
            log.atWarning()
               .withCause(e)
               .log("Could not get the output stream from the client socket.");
            setHandler(exchange, new DoNothingHandler());
            return exchange;
        }

        InputStream clientInputStream;
        try {
            clientInputStream = getClientInputStream(clientSocket);
            log.atFine().log("Successfully extracted input stream from client socket.");
        } catch (InputStreamRetrievalException e) {
            log.atWarning()
               .withCause(e)
               .log("Could not get input stream from client socket.");
            return sendOffExchangePrematurely(exchange, HttpURLConnection.HTTP_INTERNAL_ERROR,
                    "Server failed to retrieve input stream from client request.");
        }

        String requestString;
        try {
            requestString = inputParser.parseInputStream(clientInputStream, StandardCharsets.UTF_8);
            log.atFine().log("Parsed the following String from the client socket input stream:\n%s", requestString);
        } catch (StreamParsingException e) {
            log.atWarning()
               .withCause(e)
               .log("Failed to parse request String");
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

        RequestLine requestLine;
        try {
            requestLine = requestLineParser.parseRequestLine(requestLineString);
            log.atFine().log("Successfully parsed the request line.");
        } catch (RequestLineParsingException e) {
            log.atWarning()
               .withCause(e)
               .log("Failed to parse request line...");
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

        val headers = headerParser.parseHeaders(requestArray);
        exchange.setRequestHeaders(headers);
        val contentLengthString = headers.getFirst(HttpHeader.CONTENT_LENGTH.toString());

        if (contentLengthString != null) {
            val requestBody = bodyParser.parseBody(requestArray);
            exchange.setRequestBody(requestBody);
        }

        log.atFine().log("Successfully parsed request data from client: %s", clientSocket);
        return exchange;
    }
}
