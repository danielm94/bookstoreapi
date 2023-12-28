package com.github.danielm94.server.handlers;

import com.github.danielm94.server.services.create.CreateBookService;
import com.github.danielm94.server.services.create.factory.ContentTypeCreateBookServiceFactory;
import com.github.danielm94.server.services.create.factory.UnsupportedContentTypeException;
import com.github.danielm94.server.services.read.GetAllBooksService;
import com.github.danielm94.server.services.read.GetBookByIdService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.val;

import java.io.IOException;
import java.util.UUID;

import static com.github.danielm94.server.HttpMethod.*;
import static com.github.danielm94.server.exchange.Attributes.BOOK_ID;
import static com.github.danielm94.server.requestdata.content.ContentType.*;
import static com.github.danielm94.server.requestdata.headers.HttpHeader.*;
import static java.lang.String.format;
import static java.net.HttpURLConnection.*;

public class BookHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        val httpMethod = getHttpMethodFromStringValue(exchange.getRequestMethod());
        val headers = exchange.getRequestHeaders();
        if (httpMethod == POST) {
            val contentTypeHeaderValue = headers.getFirst(CONTENT_TYPE.toString());
            val contentType = getContentTypeFromString(contentTypeHeaderValue);
            val serviceFactory = new ContentTypeCreateBookServiceFactory();
            CreateBookService createBookService;
            try {
                createBookService = serviceFactory.getServiceForContentType(contentType);
            } catch (UnsupportedContentTypeException e) {
                val responseMessage = format("Server does not support content type [%s] for http method [%s] at the endpoint [%s].",
                        contentType, httpMethod, exchange.getHttpContext().getPath());
                new SimpleResponseHandler(HTTP_UNSUPPORTED_TYPE, responseMessage).handle(exchange);
                return;
            }
            createBookService.createBook(exchange);
        } else if (httpMethod == GET) {
            val attributes = exchange.getHttpContext().getAttributes();
            val resourceId = (UUID) exchange.getHttpContext().getAttributes().get(BOOK_ID.toString());
            if (resourceId == null) new GetAllBooksService().get(exchange);
            else {
                attributes.remove(BOOK_ID.toString());
                new GetBookByIdService(resourceId).get(exchange);
            }
        }
    }
}
