package com.github.danielm94.server.handlers.book;

import com.github.danielm94.server.exchange.BookHttpExchange;
import com.github.danielm94.server.handlers.book.httpmethod.factory.DefaultHttpMethodBookHandlerFactory;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static com.github.danielm94.server.requestdata.method.HttpMethod.OPTIONS;
import static java.net.HttpURLConnection.HTTP_BAD_METHOD;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class BookHandlerIntegrationTest {
    private ByteArrayOutputStream outputStream;
    private BookHttpExchange exchange;
    private BookHandler bookHandler;

    @BeforeEach
    void setUp() {
        outputStream = new ByteArrayOutputStream();
        exchange = new BookHttpExchange();
        bookHandler = new BookHandler(new DefaultHttpMethodBookHandlerFactory());

        exchange.setResponseBody(outputStream);
    }

    @Test
    void bookHandlerShouldCorrectStatusCodeIfRequestUsesUnsupportedHttpMethod() throws IOException {
        exchange.setRequestMethod(OPTIONS.toString());

        bookHandler.handle(exchange);

        val response = outputStream.toString();

        assertThat(response)
                .as("Response to client should contain correct status code.")
                .contains(String.valueOf(HTTP_BAD_METHOD));
    }

    @Test
    void bookHandlerShouldCorrectMessageBodyIfRequestUsesUnsupportedHttpMethod() throws IOException {
        val unsupportedHttpMethod = OPTIONS;
        exchange.setRequestMethod(unsupportedHttpMethod.toString());

        bookHandler.handle(exchange);

        val response = outputStream.toString();

        assertThat(response)
                .as("Response to client should contain correct message.")
                .contains(BookHandler.getUnsupportedHTTPMethodMessage(unsupportedHttpMethod));
    }

}
