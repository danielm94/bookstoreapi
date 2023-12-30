package com.github.danielm94.server.handlers.book.httpmethod;

import com.github.danielm94.server.requestdata.content.UnsupportedContentTypeException;
import com.github.danielm94.server.services.create.CreateBookService;
import com.github.danielm94.server.services.create.factory.DefaultCreateBookServiceFactory;
import com.sun.net.httpserver.HttpExchange;
import lombok.NonNull;
import lombok.val;

import static com.github.danielm94.server.handlers.SimpleResponseHandler.sendResponse;
import static com.github.danielm94.server.requestdata.content.ContentType.*;
import static com.github.danielm94.server.requestdata.headers.HttpHeader.*;
import static com.github.danielm94.server.requestdata.method.HttpMethod.POST;
import static com.github.danielm94.server.requestdata.validation.RequestDataValidator.hasHeader;
import static com.github.danielm94.server.requestdata.validation.RequestDataValidator.hasRequestBody;
import static java.lang.String.format;
import static java.net.HttpURLConnection.*;

public class PostBookHandler implements HttpMethodBookHandler {

    @Override
    public void handle(@NonNull HttpExchange exchange) {
        if (!hasRequestBody(exchange)) {
            sendResponse(exchange, HTTP_BAD_REQUEST, "You must include a body in the request.");
            return;
        }
        if (!hasHeader(exchange, CONTENT_TYPE)) {
            sendResponse(exchange, HTTP_BAD_REQUEST, "You must include a Content-Type header in your request.");
            return;
        }

        val headers = exchange.getRequestHeaders();
        val contentTypeHeaderValue = headers.getFirst(CONTENT_TYPE.toString());

        val contentType = getContentTypeFromString(contentTypeHeaderValue);
        val serviceFactory = new DefaultCreateBookServiceFactory();
        CreateBookService createBookService;
        try {
            createBookService = serviceFactory.getServiceForContentType(contentType);
        } catch (UnsupportedContentTypeException e) {
            val responseMessage = format("Server does not support content type [%s] for http method [%s] at the endpoint [%s].",
                    contentType, POST, exchange.getHttpContext().getPath());
            sendResponse(exchange, HTTP_UNSUPPORTED_TYPE, responseMessage);
            return;
        }
        createBookService.createBook(exchange);
    }
}
