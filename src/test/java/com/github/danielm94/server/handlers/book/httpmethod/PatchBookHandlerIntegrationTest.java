package com.github.danielm94.server.handlers.book.httpmethod;

import com.github.danielm94.server.context.BookContext;
import com.github.danielm94.server.exchange.BookHttpExchange;
import com.github.danielm94.server.services.update.factory.DefaultPatchBookServiceFactory;
import com.sun.net.httpserver.Headers;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

import static com.github.danielm94.server.exchange.Attributes.BOOK_ID;
import static com.github.danielm94.server.handlers.book.httpmethod.PatchBookHandler.*;
import static com.github.danielm94.server.requestdata.content.ContentType.TEXT_PLAIN;
import static com.github.danielm94.server.requestdata.headers.HttpHeader.CONTENT_LENGTH;
import static com.github.danielm94.server.requestdata.headers.HttpHeader.CONTENT_TYPE;
import static com.github.danielm94.server.requestdata.method.HttpMethod.PATCH;
import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("DataFlowIssue")
class PatchBookHandlerIntegrationTest {
    private Headers headers;
    private PatchBookHandler handler;
    private BookHttpExchange exchange;
    private BookContext context;
    private ByteArrayOutputStream outputStream;

    @BeforeEach
    void setUp() {
        exchange = new BookHttpExchange();
        handler = new PatchBookHandler(new DefaultPatchBookServiceFactory());
        context = new BookContext();
        outputStream = new ByteArrayOutputStream();
        headers = new Headers();

        exchange.setHttpContext(context);
        exchange.setRequestMethod(PATCH.toString());
        exchange.setResponseBody(outputStream);
        exchange.setRequestHeaders(headers);
    }

    @Test
    void responseShouldBeSentToClientIfThereIsNoUUID() {
        handler.handle(exchange);

        val response = outputStream.toString();
        assertThat(response)
                .as("Server sends correct response to client if there is no UUID in request.")
                .contains(String.valueOf(NO_UUID_INCLUDED_STATUS_CODE))
                .contains(NO_UUID_INCLUDED_RESPONSE_MESSAGE);
    }

    @Test
    void responseShouldBeSentToClientIfThereIsNoRequestBody() {
        exchange.setAttribute(BOOK_ID.toString(), UUID.randomUUID());

        headers.add(CONTENT_LENGTH.toString(), "0");

        handler.handle(exchange);

        val response = outputStream.toString();
        assertThat(response)
                .as("Server sends correct response to client if there is no body in the request.")
                .contains(String.valueOf(NO_REQUEST_BODY_STATUS_CODE))
                .contains(NO_REQUEST_BODY_RESPONSE_MESSAGE);
    }

    @Test
    void responseShouldBeSentToClientIfContentTypeIsMissing() {
        exchange.setAttribute(BOOK_ID.toString(), UUID.randomUUID());

        headers.add(CONTENT_LENGTH.toString(), "10");


        handler.handle(exchange);

        val response = outputStream.toString();
        assertThat(response)
                .as("Server sends correct response to client if the content type header is missing.")
                .contains(String.valueOf(MISSING_CONTENT_TYPE_STATUS_CODE))
                .contains(MISSING_CONTENT_TYPE_RESPONSE_MESSAGE);
    }

    @Test
    void responseShouldBeSentToClientIfContentTypeIsUnlisted() {
        exchange.setAttribute(BOOK_ID.toString(), UUID.randomUUID());

        headers.add(CONTENT_LENGTH.toString(), "10");

        val contentTypeHeaderValue = "not an actual content type";
        headers.add(CONTENT_TYPE.toString(), contentTypeHeaderValue);

        handler.handle(exchange);

        val response = outputStream.toString();
        assertThat(response)
                .as("Server sends correct response to client if the content type provided is not listed in the ContentType.java enum.")
                .contains(String.valueOf(UNSUPPORTED_CONTENT_TYPE_STATUS_CODE))
                .contains(getUnsupportedContentTypeResponseMessage(contentTypeHeaderValue));
    }

    @Test
    void responseShouldBeSentToClientIfNoPatchServiceIsAvailableForContentType() {
        exchange.setAttribute(BOOK_ID.toString(), UUID.randomUUID());

        headers.add(CONTENT_LENGTH.toString(), "10");

        val contentType = TEXT_PLAIN;
        headers.add(CONTENT_TYPE.toString(), contentType.toString());

        handler.handle(exchange);

        val response = outputStream.toString();
        assertThat(response)
                .as("Server sends correct response to client if there is no patch service matching the content type provided.")
                .contains(String.valueOf(NO_PATCH_SERVICE_FOR_CONTENT_TYPE_STATUS_CODE))
                .contains(getNoPatchServiceForContentTypeResponseMessage(exchange, contentType));
    }

}