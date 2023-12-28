package com.github.danielm94.server.handlers;

import com.github.danielm94.server.requestdata.content.ContentType;
import com.github.danielm94.server.services.create.CreateBookService;
import com.github.danielm94.server.services.create.factory.ContentTypeCreateBookServiceFactory;
import com.github.danielm94.server.services.create.factory.UnsupportedContentTypeException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.val;

import java.io.IOException;
import java.util.UUID;

import static com.github.danielm94.server.HttpMethod.POST;
import static com.github.danielm94.server.HttpMethod.getHttpMethodFromStringValue;
import static com.github.danielm94.server.requestdata.headers.HttpHeader.CONTENT_TYPE;
import static java.net.HttpURLConnection.HTTP_UNSUPPORTED_TYPE;

public class BookHandler implements HttpHandler {
    private final UUID resourceId;

    public BookHandler() {
        this.resourceId = null;
    }

    public BookHandler(UUID resourceId) {
        this.resourceId = resourceId;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        val httpMethod = getHttpMethodFromStringValue(exchange.getRequestMethod());
        val headers = exchange.getRequestHeaders();
        if (httpMethod == POST) {
            val contentTypeHeaderValue = headers.getFirst(CONTENT_TYPE.toString());
            val contentType = ContentType.getContentTypeFromString(contentTypeHeaderValue);
            val serviceFactory = new ContentTypeCreateBookServiceFactory();
            CreateBookService createBookService;
            try {
                createBookService = serviceFactory.getServiceForContentType(contentType);
            } catch (UnsupportedContentTypeException e) {
                val responseMessage = String.format("Server does not support content type [%s] for http method [%s] at the endpoint [%s].",
                        contentType, httpMethod, exchange.getHttpContext().getPath());
                new SimpleResponseHandler(HTTP_UNSUPPORTED_TYPE, responseMessage).handle(exchange);
                return;
            }
            createBookService.createBook(exchange);
        }
    }
}
