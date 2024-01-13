package com.github.danielm94.server.handlers.book.httpmethod;

import com.github.danielm94.server.requestdata.content.ContentType;
import com.github.danielm94.server.requestdata.content.UnsupportedContentTypeException;
import com.github.danielm94.server.services.update.PutBookService;
import com.github.danielm94.server.services.update.factory.PutBookServiceFactory;
import com.sun.net.httpserver.HttpExchange;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.val;

import static com.github.danielm94.server.exchange.Attributes.BOOK_ID;
import static com.github.danielm94.server.handlers.SimpleResponseHandler.sendResponse;
import static com.github.danielm94.server.requestdata.content.ContentType.*;
import static com.github.danielm94.server.requestdata.headers.HttpHeader.*;
import static com.github.danielm94.server.requestdata.method.HttpMethod.PUT;
import static com.github.danielm94.server.requestdata.validation.RequestDataValidator.*;
import static java.lang.String.format;
import static java.net.HttpURLConnection.*;
import static java.util.UUID.fromString;

@AllArgsConstructor
public class PutBookHandler implements HttpMethodBookHandler {
    public static final int NO_UUID_INCLUDED_STATUS_CODE = HTTP_BAD_REQUEST;
    public static final String NO_UUID_INCLUDED_RESPONSE_MESSAGE = "You must specify the UUID of the book you wish to update in the URI.";
    public static final int NO_REQUEST_BODY_STATUS_CODE = HTTP_BAD_REQUEST;
    public static final String NO_REQUEST_BODY_RESPONSE_MESSAGE = "You must include a body in the request.";
    public static final int MISSING_CONTENT_TYPE_STATUS_CODE = HTTP_BAD_REQUEST;
    public static final String MISSING_CONTENT_TYPE_RESPONSE_MESSAGE = "You must include a Content-Type header in your request.";
    public static final int UNSUPPORTED_CONTENT_TYPE_STATUS_CODE = HTTP_BAD_REQUEST;
    public static final int NO_PUT_SERVICE_FOR_CONTENT_TYPE_STATUS_CODE = HTTP_UNSUPPORTED_TYPE;
    @NonNull
    private final PutBookServiceFactory factory;

    public static String getNoPutServiceForContentTypeResponseMessage(HttpExchange exchange, ContentType contentType) {
        return format("Server does not support content type [%s] for http method [%s] at the endpoint [%s].",
                contentType, PUT, exchange.getHttpContext().getPath());
    }

    public static String getUnsupportedContentTypeResponseMessage(String contentTypeHeaderValue) {
        return contentTypeHeaderValue + " is not a supported content type.";
    }

    @Override
    public void handle(@NonNull HttpExchange exchange) {
        if (!hasAttribute(exchange, BOOK_ID)) {
            sendResponse(exchange, NO_UUID_INCLUDED_STATUS_CODE, NO_UUID_INCLUDED_RESPONSE_MESSAGE);
            return;
        }
        if (!hasRequestBody(exchange)) {
            sendResponse(exchange, NO_REQUEST_BODY_STATUS_CODE, NO_REQUEST_BODY_RESPONSE_MESSAGE);
            return;
        }
        if (!hasHeader(exchange, CONTENT_TYPE)) {
            sendResponse(exchange, MISSING_CONTENT_TYPE_STATUS_CODE, MISSING_CONTENT_TYPE_RESPONSE_MESSAGE);
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

        PutBookService putBookService;
        try {
            putBookService = factory.getService(contentType);
        } catch (UnsupportedContentTypeException e) {
            sendResponse(exchange, NO_PUT_SERVICE_FOR_CONTENT_TYPE_STATUS_CODE, getNoPutServiceForContentTypeResponseMessage(exchange, contentType));
            return;
        }

        val attributes = exchange.getHttpContext().getAttributes();
        val uuidString = attributes.get(BOOK_ID.toString()).toString();
        val uuid = fromString(uuidString);
        putBookService.updateBook(exchange, uuid);
    }
}
