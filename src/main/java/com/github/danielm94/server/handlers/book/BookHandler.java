package com.github.danielm94.server.handlers.book;

import com.github.danielm94.server.requestdata.content.ContentType;
import com.github.danielm94.server.requestdata.content.UnsupportedContentTypeException;
import com.github.danielm94.server.requestdata.method.HttpMethod;
import com.github.danielm94.server.services.create.CreateBookService;
import com.github.danielm94.server.services.create.factory.DefaultCreateBookServiceFactory;
import com.github.danielm94.server.services.read.GetAllBooksService;
import com.github.danielm94.server.services.read.GetBookByIdService;
import com.github.danielm94.server.services.update.PutBookService;
import com.github.danielm94.server.services.update.factory.DefaultPutBookServiceFactory;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.NonNull;
import lombok.extern.flogger.Flogger;
import lombok.val;

import java.util.UUID;

import static com.github.danielm94.server.exchange.Attributes.BOOK_ID;
import static com.github.danielm94.server.handlers.SimpleResponseHandler.sendResponse;
import static com.github.danielm94.server.requestdata.content.ContentType.*;
import static com.github.danielm94.server.requestdata.headers.HttpHeader.*;
import static com.github.danielm94.server.requestdata.method.HttpMethod.getHttpMethodFromStringValue;
import static java.lang.String.format;
import static java.net.HttpURLConnection.*;

@Flogger
public class BookHandler implements HttpHandler {

    private static void handleUnsupportedContentType(@NonNull HttpExchange exchange, ContentType contentType, HttpMethod httpMethod) {
        val responseMessage = format("Server does not support content type [%s] for http method [%s] at the endpoint [%s].",
                contentType, httpMethod, exchange.getHttpContext().getPath());
        sendResponse(exchange, HTTP_UNSUPPORTED_TYPE, responseMessage);
    }

    private static boolean hasRequestBody(@NonNull HttpExchange exchange) {
        val requestHeaders = exchange.getRequestHeaders();
        if (requestHeaders.containsKey(CONTENT_LENGTH.toString())) {
            try {
                int contentLength = Integer.parseInt(requestHeaders.getFirst(CONTENT_LENGTH.toString()));
                return contentLength > 0;
            } catch (NumberFormatException e) {
                log.atWarning().withCause(e).log("Failed to parse Content-Length header.");
            }
        }
        return false;
    }

    @Override
    public void handle(HttpExchange exchange) {
        val httpMethod = getHttpMethodFromStringValue(exchange.getRequestMethod());
        val headers = exchange.getRequestHeaders();
        switch (httpMethod) {
            case POST -> {
                if (!hasRequestBody(exchange)) {
                    sendResponse(exchange, HTTP_BAD_REQUEST, "You must include a body in the request.");
                    return;
                }
                val contentTypeHeaderValue = headers.getFirst(CONTENT_TYPE.toString());
                if (contentTypeHeaderValue == null) {
                    sendResponse(exchange, HTTP_BAD_REQUEST, "You must include a Content-Type header in your request.");
                    return;
                }
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
                if (!hasRequestBody(exchange)) {
                    sendResponse(exchange, HTTP_BAD_REQUEST, "You must include a body in the request.");
                    return;
                }
                val attributes = exchange.getHttpContext().getAttributes();
                val resourceId = (UUID) attributes.get(BOOK_ID.toString());
                if (resourceId == null) {
                    sendResponse(exchange, HTTP_BAD_REQUEST, "You must specify the UUID of the book you wish to update in the URI.");
                    return;
                }

                val contentTypeHeaderValue = headers.getFirst(CONTENT_TYPE.toString());
                if (contentTypeHeaderValue == null) {
                    sendResponse(exchange, HTTP_BAD_REQUEST, "You must include a Content-Type header in your request.");
                    return;
                }
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
