package com.github.danielm94.server.handlers.book.httpmethod;

import com.github.danielm94.server.context.BookContext;
import com.github.danielm94.server.domain.book.serializer.factory.DefaultBookSerializerFactory;
import com.github.danielm94.server.exchange.BookHttpExchange;
import com.github.danielm94.server.services.read.BookRetrievalService;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static com.github.danielm94.server.handlers.book.httpmethod.GetBookHandler.*;
import static com.github.danielm94.server.requestdata.content.ContentType.TEXT_PLAIN;
import static com.github.danielm94.server.requestdata.headers.HttpHeader.ACCEPT;
import static com.github.danielm94.server.requestdata.method.HttpMethod.GET;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class GetBookHandlerIntegrationTest {
    private GetBookHandler handler;
    private BookHttpExchange exchange;
    private ByteArrayOutputStream outputStream;

    @BeforeEach
    void setUp() {
        handler = new GetBookHandler(new BookRetrievalService(), new DefaultBookSerializerFactory());
        exchange = new BookHttpExchange();
        BookContext context = new BookContext();
        outputStream = new ByteArrayOutputStream();

        exchange.setRequestMethod(GET.toString());
        exchange.setHttpContext(context);
        exchange.setResponseBody(outputStream);
    }


    @Test
    void clientShouldReceiveCorrectStatusCodeWhenTheySendRequestWithNoAcceptHeader() throws IOException {
        handler.handle(exchange);

        val response = outputStream.toString();
        val responseCode = GetBookHandler.MISSING_ACCEPT_HEADER_STATUS_CODE;
        assertThat(response)
                .as("Client should receive status code of %s when they do not include Accept header.", responseCode)
                .contains(String.valueOf(responseCode));
    }

    @Test
    void clientShouldReceiveCorrectResponseBodyWhenTheySendRequestWithNoAcceptHeader() throws IOException {
        handler.handle(exchange);

        val response = outputStream.toString();
        val responseBody = MISSING_ACCEPT_HEADER_MESSAGE;
        assertThat(response)
                .as("Client should receive response body [%s] when they do not include Accept header.", responseBody)
                .contains(responseBody);
    }

    @Test
    void clientShouldReceiveCorrectStatusCodeWhenTheySendRequestWithUnsupportedContentType() throws IOException {
        val acceptHeaderValue = "askdajskldja";
        exchange.getRequestHeaders().add(ACCEPT.toString(), acceptHeaderValue);
        handler.handle(exchange);

        val response = outputStream.toString();
        val responseCode = UNSUPPORTED_CONTENT_TYPE_STATUS_CODE;
        assertThat(response)
                .as("Client should receive status code of %s when they provide an unsupported content type.", responseCode)
                .contains(String.valueOf(responseCode));
    }

    @Test
    void clientShouldReceiveCorrectMessageWhenTheySendRequestWithUnsupportedContentType() throws IOException {
        val acceptHeaderValue = "askdajskldja";
        exchange.getRequestHeaders().add(ACCEPT.toString(), acceptHeaderValue);
        handler.handle(exchange);

        val response = outputStream.toString();
        val responseBody = getUnsupportedContentTypeResponseBody(acceptHeaderValue);
        assertThat(response)
                .as("Client should receive response body of [%s] when they provide an unsupported content type.", responseBody)
                .contains(responseBody);
    }

    @Test
    void clientShouldReceiveCorrectStatusCodeWhenTheySendRequestAskingForUnsupportedSerializer() throws IOException {
        exchange.getRequestHeaders().add(ACCEPT.toString(), TEXT_PLAIN.toString());
        handler.handle(exchange);

        val response = outputStream.toString();
        val statusCode = UNSUPPORTED_SERIALIZER_FORMAT_STATUS_CODE;
        assertThat(response)
                .as("Client should receive status code of %s when they provide an unsupported content type.", statusCode)
                .contains(String.valueOf(statusCode));
    }

    @Test
    void clientShouldReceiveCorrectResponseBodyWhenTheySendRequestAskingForUnsupportedSerializer() throws IOException {
        val unsupportedSerializerContentType = TEXT_PLAIN;
        exchange.getRequestHeaders().add(ACCEPT.toString(), unsupportedSerializerContentType.toString());
        handler.handle(exchange);

        val response = outputStream.toString();
        val responseBody = getUnsupportedSerializerFormatResponseBody(unsupportedSerializerContentType);
        assertThat(response)
                .as("Client should receive response body of [%s] when they provide an unsupported content type.", responseBody)
                .contains(responseBody);
    }


}
