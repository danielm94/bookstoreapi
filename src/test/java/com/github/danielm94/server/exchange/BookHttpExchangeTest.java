package com.github.danielm94.server.exchange;

import com.sun.net.httpserver.Headers;
import lombok.val;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static com.github.danielm94.server.requestdata.headers.HttpHeader.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class BookHttpExchangeTest {
    private ByteArrayOutputStream outputStream;
    private BookHttpExchange exchange;

    @BeforeEach
    void setUp() {
        outputStream = new ByteArrayOutputStream();
        exchange = new BookHttpExchange();
        exchange.setResponseBody(outputStream);
    }


    @Test
    void sendResponseHeadersShouldIncludeContentLength() throws IOException {
        val responseLength = 500;
        exchange.sendResponseHeaders(0, responseLength);

        val responseBody = getResponseString();

        val expectedLine = CONTENT_LENGTH + ": " + responseLength;
        assertThat(StringUtils.containsAnyIgnoreCase(responseBody, expectedLine))
                .as("sendResponseHeaders should include content length in output stream")
                .isTrue();
    }

    @Test
    void sendResponseHeadersShouldIncludeProtocolAndResponseCode() throws IOException {
        val code = 200;
        val protocol = "HTTP";

        exchange.setProtocol(protocol);
        exchange.sendResponseHeaders(code, 0);

        val responseBody = getResponseString();
        val expectedLine = protocol + " " + code;
        assertThat(StringUtils.containsAnyIgnoreCase(responseBody, expectedLine))
                .as("sendResponseHeaders should include protocol and status code in output stream")
                .isTrue();
    }

    @Test
    void sendResponseHeadersShouldIncludeEmptyLineAtTheEnd() throws IOException {
        exchange.sendResponseHeaders(200, 0);

        val responseBody = getResponseString();

        assertThat(responseBody)
                .as("sendResponseHeaders should include empty line at the end of the response.")
                .endsWith(System.lineSeparator());
    }

    @Test
    void sendResponseHeadersShouldIncludeAllResponseHeadersInTheExchange() throws IOException {
        val headers = new Headers();
        for (var i = 0; i < 10; i++) {
            val key = "Key " + i;
            val value = "Value " + i;
            headers.add(key, value);
        }

        exchange.setResponseHeaders(headers);
        exchange.sendResponseHeaders(200, 0);
        val response = getResponseString();

        for (val key : headers.keySet()) {
            val value = headers.get(key).getFirst();
            val expectedLine = key + ": " + value;

            assertThat(response)
                    .as("sendResponseHeaders should include all of the response headers in the exchange.")
                    .contains(expectedLine);
        }
    }

    private String getResponseString() throws IOException {
        val inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        val response = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        if (outputStream != null) outputStream.close();
        return response;
    }
}