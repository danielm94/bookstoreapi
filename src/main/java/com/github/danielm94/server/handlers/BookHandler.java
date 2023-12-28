package com.github.danielm94.server.handlers;

import com.github.danielm94.server.HttpMethod;
import com.github.danielm94.server.requestdata.content.ContentType;
import com.github.danielm94.server.requestdata.headers.HttpHeader;
import com.github.danielm94.server.services.create.CreateBookService;
import com.github.danielm94.server.services.create.factory.ContentTypeCreateBookServiceFactory;
import com.github.danielm94.server.services.create.factory.UnsupportedContentTypeException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.val;

import java.io.IOException;
import java.net.HttpURLConnection;

public class BookHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        val httpMethod = HttpMethod.getHttpMethodFromStringValue(exchange.getRequestMethod());
        val headers = exchange.getRequestHeaders();
        if (httpMethod == HttpMethod.POST) {
            val contentTypeHeaderValue = headers.getFirst(HttpHeader.CONTENT_TYPE.toString());
            val contentType = ContentType.getContentTypeFromString(contentTypeHeaderValue);
            val serviceFactory = new ContentTypeCreateBookServiceFactory();
            CreateBookService createBookService;
            try {
                createBookService = serviceFactory.getServiceForContentType(contentType);
            } catch (UnsupportedContentTypeException e) {
                val responseMessage = String.format("Server does not support content type [%s] for http method [%s] at the endpoint [%s].",
                        contentType, httpMethod, exchange.getHttpContext().getPath());
                new SimpleResponseHandler(HttpURLConnection.HTTP_UNSUPPORTED_TYPE, responseMessage).handle(exchange);
                return;
            }
            createBookService.createBook(exchange);
        }
    }
}
