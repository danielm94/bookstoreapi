package com.github.danielm94.server.handlers.book;

import com.github.danielm94.server.exchange.BookHttpExchange;
import com.github.danielm94.server.handlers.book.httpmethod.factory.DefaultHttpMethodBookHandlerFactory;
import com.github.danielm94.server.handlers.book.httpmethod.factory.HttpMethodBookHandlerFactory;
import lombok.val;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static com.github.danielm94.server.requestdata.method.HttpMethod.OPTIONS;
import static java.net.HttpURLConnection.HTTP_BAD_METHOD;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SuppressWarnings("DataFlowIssue")
public class BookHandlerIntegrationTest {
    private ByteArrayOutputStream outputStream;
    private HttpMethodBookHandlerFactory factory;
    private BookHttpExchange exchange;
    private BookHandler bookHandler;

    @BeforeEach
    void setUp() {
        outputStream = new ByteArrayOutputStream();
        factory = new DefaultHttpMethodBookHandlerFactory();
        exchange = new BookHttpExchange();
        bookHandler = new BookHandler(factory);

        exchange.setResponseBody(outputStream);
    }

    @Test
    void bookHandlerShouldSendResponseToClientIfRequestUsesUnsupportedHttpMethod() throws IOException {
        exchange.setRequestMethod(OPTIONS.toString());

        bookHandler.handle(exchange);

        val response = getResponseString(outputStream);

        assertThat(response)
                .as("Response to client should contain correct status code.")
                .contains(String.valueOf(HTTP_BAD_METHOD));
    }

    private String getResponseString(ByteArrayOutputStream outputStream) throws IOException {
        val inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        val response = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        outputStream.close();
        return response;
    }
}
