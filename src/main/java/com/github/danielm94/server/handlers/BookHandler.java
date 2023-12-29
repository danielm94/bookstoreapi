package com.github.danielm94.server.handlers;

import com.github.danielm94.server.HttpMethod;
import com.github.danielm94.server.requestdata.content.ContentType;
import com.github.danielm94.server.services.create.CreateBookService;
import com.github.danielm94.server.services.create.factory.DefaultCreateBookServiceFactory;
import com.github.danielm94.server.services.exceptions.UnsupportedContentTypeException;
import com.github.danielm94.server.services.read.GetAllBooksService;
import com.github.danielm94.server.services.read.GetBookByIdService;
import com.github.danielm94.server.services.update.PutBookService;
import com.github.danielm94.server.services.update.factory.DefaultPutBookServiceFactory;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.val;

import java.io.IOException;
import java.util.UUID;

import static com.github.danielm94.server.HttpMethod.getHttpMethodFromStringValue;
import static com.github.danielm94.server.exchange.Attributes.BOOK_ID;
import static com.github.danielm94.server.handlers.SimpleResponseHandler.sendResponse;
import static com.github.danielm94.server.requestdata.content.ContentType.*;
import static com.github.danielm94.server.requestdata.headers.HttpHeader.*;
import static java.lang.String.format;
import static java.net.HttpURLConnection.*;

public class BookHandler implements HttpHandler {

    private static void handleUnsupportedContentType(HttpExchange exchange, ContentType contentType, HttpMethod httpMethod) {
        val responseMessage = format("Server does not support content type [%s] for http method [%s] at the endpoint [%s].",
                contentType, httpMethod, exchange.getHttpContext().getPath());
        sendResponse(exchange, HTTP_UNSUPPORTED_TYPE, responseMessage);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        val httpMethod = getHttpMethodFromStringValue(exchange.getRequestMethod());
        val headers = exchange.getRequestHeaders();
        switch (httpMethod) {
            case POST -> {
                val contentTypeHeaderValue = headers.getFirst(CONTENT_TYPE.toString());
                val contentType = getContentTypeFromString(contentTypeHeaderValue);
                val serviceFactory = new DefaultCreateBookServiceFactory();
                CreateBookService createBookService;
                try {
                    createBookService = serviceFactory.getServiceForContentType(contentType);
                } catch (UnsupportedContentTypeException e) {
                    handleUnsupportedContentType(exchange, contentType, httpMethod);
                    return;
                }
                createBookService.createBook(exchange);
            }
            case GET -> {
                val attributes = exchange.getHttpContext().getAttributes();
                val resourceId = (UUID) attributes.get(BOOK_ID.toString());
                if (resourceId == null) new GetAllBooksService().get(exchange);
                else {
                    attributes.remove(BOOK_ID.toString());
                    new GetBookByIdService(resourceId).get(exchange);
                }
            }
            case PUT -> {
                val attributes = exchange.getHttpContext().getAttributes();
                val resourceId = (UUID) attributes.get(BOOK_ID.toString());
                if (resourceId == null) {
                    sendResponse(exchange, HTTP_BAD_REQUEST, "You must specify the UUID of the book you wish to update in the URI.");
                    return;
                }

                val contentTypeHeaderValue = headers.getFirst(CONTENT_TYPE.toString());
                val contentType = getContentTypeFromString(contentTypeHeaderValue);
                val factory = new DefaultPutBookServiceFactory();

                PutBookService putBookService;
                try {
                    putBookService = factory.getPutBookService(contentType);
                } catch (UnsupportedContentTypeException e) {
                    handleUnsupportedContentType(exchange, contentType, httpMethod);
                    return;
                }

                putBookService.updateBook(exchange, resourceId);
            }
        }
    }
}
