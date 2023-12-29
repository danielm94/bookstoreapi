package com.github.danielm94.server.handlers.book.httpmethod;

import com.github.danielm94.server.requestdata.content.UnsupportedContentTypeException;
import com.github.danielm94.server.services.update.PutBookService;
import com.github.danielm94.server.services.update.factory.DefaultPutBookServiceFactory;
import com.sun.net.httpserver.HttpExchange;
import lombok.NonNull;
import lombok.val;

import java.util.UUID;

import static com.github.danielm94.server.exchange.Attributes.BOOK_ID;
import static com.github.danielm94.server.handlers.SimpleResponseHandler.sendResponse;
import static com.github.danielm94.server.requestdata.content.ContentType.*;
import static com.github.danielm94.server.requestdata.headers.HttpHeader.*;
import static com.github.danielm94.server.requestdata.method.HttpMethod.PUT;
import static com.github.danielm94.server.requestdata.validation.RequestDataValidator.*;
import static java.lang.String.format;
import static java.net.HttpURLConnection.*;

public class PutBookHandler implements HttpMethodBookHandler {
    @Override
    public void handle(@NonNull HttpExchange exchange) {
        if (!hasRequestBody(exchange)) {
            sendResponse(exchange, HTTP_BAD_REQUEST, "You must include a body in the request.");
            return;
        }

        val attributes = exchange.getHttpContext().getAttributes();
        val resourceId = (UUID) attributes.get(BOOK_ID.toString());
        if (!hasAttribute(exchange, BOOK_ID)) {
            sendResponse(exchange, HTTP_BAD_REQUEST, "You must specify the UUID of the book you wish to update in the URI.");
            return;
        }


        val contentTypeHeaderValue = exchange.getRequestHeaders().getFirst(CONTENT_TYPE.toString());
        if (!hasHeader(exchange, CONTENT_TYPE)) {
            sendResponse(exchange, HTTP_BAD_REQUEST, "You must include a Content-Type header in your request.");
            return;
        }

        val contentType = getContentTypeFromString(contentTypeHeaderValue);
        val factory = new DefaultPutBookServiceFactory();

        PutBookService putBookService;
        try {
            putBookService = factory.getPutBookService(contentType);
        } catch (UnsupportedContentTypeException e) {
            val responseMessage = format("Server does not support content type [%s] for http method [%s] at the endpoint [%s].",
                    contentType, PUT, exchange.getHttpContext().getPath());
            sendResponse(exchange, HTTP_UNSUPPORTED_TYPE, responseMessage);
            return;
        }

        putBookService.updateBook(exchange, resourceId);
    }
}