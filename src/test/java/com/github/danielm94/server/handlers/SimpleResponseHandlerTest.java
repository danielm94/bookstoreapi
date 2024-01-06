package com.github.danielm94.server.handlers;

import com.github.danielm94.server.exchange.BookHttpExchange;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import static com.github.danielm94.server.handlers.SimpleResponseHandler.sendResponse;
import static com.github.danielm94.server.requestdata.headers.HttpHeader.CONTENT_LENGTH;
import static com.github.danielm94.server.requestdata.method.HttpMethod.GET;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class SimpleResponseHandlerTest {
    private ByteArrayOutputStream outputStream;
    private BookHttpExchange exchange;


    @BeforeEach
    void setUp() {
        this.exchange = new BookHttpExchange();
        this.outputStream = new ByteArrayOutputStream();
        exchange.setStreams(null, outputStream);
        exchange.setRequestMethod(GET.toString());
    }

    @Test
    void sendResponseWithBody() throws IOException {
        val statusCode = 200;
        val body = "Test";

        val bodyContentLength = body.getBytes(StandardCharsets.UTF_8).length;
        val contentLengthLine = CONTENT_LENGTH + ": " + bodyContentLength;

        sendResponse(exchange, statusCode, body);
        val responseStr = outputStream.toString();

        assertThat(responseStr)
                .as("Response should contain the status code, the body content, and a correct Content-Length header")
                .contains(String.valueOf(statusCode))
                .contains(body)
                .containsIgnoringCase(contentLengthLine);
    }

    @Test
    void sendResponseWithNoBody() throws IOException {
        val statusCode = 200;
        val contentLengthLine = CONTENT_LENGTH + ": 0";

        sendResponse(exchange, statusCode, null);
        val responseStr = outputStream.toString();

        assertThat(responseStr)
                .as("Response with no body should contain the correct status code, a Content-Length header set to 0, and end with an empty line to signify the end of the headers section")
                .contains(String.valueOf(statusCode))
                .containsIgnoringCase(contentLengthLine)
                .endsWith(System.lineSeparator());
    }


    @Test
    void handleShouldCallSendResponseInternal() throws IOException {
        int statusCode = 200;
        String body = "Test Body";

        val exchange = mock(BookHttpExchange.class);

        when(exchange.getRequestMethod()).thenReturn(GET.toString());
        when(exchange.getResponseBody()).thenReturn(mock(OutputStream.class));

        val handler = new SimpleResponseHandler(statusCode, body);
        handler.handle(exchange);

        verify(exchange).sendResponseHeaders(eq(statusCode), anyLong());
        verify(exchange.getResponseBody()).write(any(byte[].class));
    }

}