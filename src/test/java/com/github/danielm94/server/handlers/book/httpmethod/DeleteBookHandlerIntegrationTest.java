package com.github.danielm94.server.handlers.book.httpmethod;

import com.github.danielm94.server.context.BookContext;
import com.github.danielm94.server.exchange.BookHttpExchange;
import com.github.danielm94.server.services.delete.BookRemovalService;
import lombok.val;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static com.github.danielm94.server.requestdata.method.HttpMethod.DELETE;
import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SuppressWarnings("DataFlowIssue")
public class DeleteBookHandlerIntegrationTest {
    private ByteArrayOutputStream outputStream;
    private BookHttpExchange exchange;
    private DeleteBookHandler deleteBookHandler;

    @BeforeEach
    void setUp() {
        outputStream = new ByteArrayOutputStream();
        exchange = new BookHttpExchange();
        deleteBookHandler = new DeleteBookHandler(new BookRemovalService());

        exchange.setResponseBody(outputStream);
        exchange.setRequestMethod(DELETE.toString());
        exchange.setHttpContext(new BookContext());
    }

    @Test
    void handleShouldSendResponseToClientIfNoBookIDIsSpecified() throws IOException {
        deleteBookHandler.handle(exchange);

        val response = getResponseString(outputStream);

        assertThat(response)
                .as("Response to client should contain correct status code.")
                .contains(String.valueOf(HTTP_BAD_REQUEST));
    }

    private String getResponseString(ByteArrayOutputStream outputStream) throws IOException {
        val inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        val response = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        outputStream.close();
        return response;
    }
}
