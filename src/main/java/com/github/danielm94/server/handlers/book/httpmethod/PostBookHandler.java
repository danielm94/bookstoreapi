package com.github.danielm94.server.handlers.book.httpmethod;

import com.github.danielm94.server.requestdata.content.ContentType;
import com.github.danielm94.server.requestdata.content.UnsupportedContentTypeException;
import com.github.danielm94.server.services.create.CreateBookService;
import com.github.danielm94.server.services.create.factory.CreateBookServiceFactory;
import com.sun.net.httpserver.HttpExchange;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.val;

import static com.github.danielm94.server.handlers.SimpleResponseHandler.sendResponse;
import static com.github.danielm94.server.requestdata.content.ContentType.getContentTypeFromString;
import static com.github.danielm94.server.requestdata.headers.HttpHeader.CONTENT_TYPE;
import static com.github.danielm94.server.requestdata.method.HttpMethod.POST;
import static com.github.danielm94.server.requestdata.validation.RequestDataValidator.hasHeader;
import static com.github.danielm94.server.requestdata.validation.RequestDataValidator.hasRequestBody;
import static java.lang.String.format;
import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_UNSUPPORTED_TYPE;

@AllArgsConstructor
public class PostBookHandler implements HttpMethodBookHandler {
    public static final int NO_REQUEST_BODY_STATUS_CODE = HTTP_BAD_REQUEST;
    public static final String NO_REQUEST_BODY_RESPONSE_MESSAGE = "You must include a body in the request.";
    public static final int NO_CONTENT_TYPE_HEADER_STATUS_CODE = HTTP_BAD_REQUEST;
    public static final String NO_CONTENT_TYPE_HEADER_RESPONSE_MESSAGE = "You must include a Content-Type header in your request.";
    public static final int UNSUPPORTED_CONTENT_TYPE_STATUS_CODE = HTTP_BAD_REQUEST;
    public static final int NO_SERVICE_FOR_REQUESTED_CONTENT_TYPE_STATUS_CODE = HTTP_UNSUPPORTED_TYPE;
    @NonNull
    private final CreateBookServiceFactory serviceFactory;

    public static String getNoServiceForRequestContentTypeResponseMessage(HttpExchange exchange, ContentType contentType) {
        return format("Server does not support content type [%s] for http method [%s] at the endpoint [%s].",
                contentType, POST, exchange.getHttpContext().getPath());
    }

    public static String getUnsupportedContentTypeResponseMessage(String contentTypeHeaderValue) {
        return contentTypeHeaderValue + " is not a supported content type.";
    }

    @Override
    public void handle(@NonNull HttpExchange exchange) {
        if (!hasRequestBody(exchange)) {
            sendResponse(exchange, NO_REQUEST_BODY_STATUS_CODE, NO_REQUEST_BODY_RESPONSE_MESSAGE);
            return;
        }
        if (!hasHeader(exchange, CONTENT_TYPE)) {
            sendResponse(exchange, NO_CONTENT_TYPE_HEADER_STATUS_CODE, NO_CONTENT_TYPE_HEADER_RESPONSE_MESSAGE);
            return;
        }

        val headers = exchange.getRequestHeaders();
        val contentTypeHeaderValue = headers.getFirst(CONTENT_TYPE.toString());

        ContentType contentType;
        try {
            contentType = getContentTypeFromString(contentTypeHeaderValue);
        } catch (UnsupportedContentTypeException e) {
            sendResponse(exchange, UNSUPPORTED_CONTENT_TYPE_STATUS_CODE, getUnsupportedContentTypeResponseMessage(contentTypeHeaderValue));
            return;
        }

        CreateBookService createBookService;
        try {
            createBookService = serviceFactory.getService(contentType);
        } catch (UnsupportedContentTypeException e) {
            sendResponse(exchange, NO_SERVICE_FOR_REQUESTED_CONTENT_TYPE_STATUS_CODE, getNoServiceForRequestContentTypeResponseMessage(exchange, contentType));
            return;
        }
        createBookService.createBook(exchange);
    }
}
