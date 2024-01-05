package com.github.danielm94.server.handlers.book.httpmethod;

import com.github.danielm94.server.requestdata.content.ContentType;
import com.github.danielm94.server.requestdata.content.UnsupportedContentTypeException;
import com.github.danielm94.server.services.update.PatchBookService;
import com.github.danielm94.server.services.update.factory.PatchBookServiceFactory;
import com.sun.net.httpserver.HttpExchange;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.val;

import static com.github.danielm94.server.exchange.Attributes.BOOK_ID;
import static com.github.danielm94.server.handlers.SimpleResponseHandler.sendResponse;
import static com.github.danielm94.server.requestdata.content.ContentType.getContentTypeFromString;
import static com.github.danielm94.server.requestdata.headers.HttpHeader.CONTENT_TYPE;
import static com.github.danielm94.server.requestdata.method.HttpMethod.PATCH;
import static com.github.danielm94.server.requestdata.validation.RequestDataValidator.hasAttribute;
import static com.github.danielm94.server.requestdata.validation.RequestDataValidator.hasRequestBody;
import static java.lang.String.format;
import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_UNSUPPORTED_TYPE;
import static java.util.UUID.fromString;

@AllArgsConstructor
public class PatchBookHandler implements HttpMethodBookHandler {
    public static final int NO_UUID_INCLUDED_STATUS_CODE = HTTP_BAD_REQUEST;
    public static final String NO_UUID_INCLUDED_RESPONSE_MESSAGE = "You must specify the UUID of the book in the path.";
    public static final int NO_REQUEST_BODY_STATUS_CODE = HTTP_BAD_REQUEST;
    public static final String NO_REQUEST_BODY_RESPONSE_MESSAGE = "You must include a body of some type in your request in order to patch a book.";
    public static final int UNSUPPORTED_CONTENT_TYPE_STATUS_CODE = HTTP_BAD_REQUEST;
    public static final int NO_PATCH_SERVICE_FOR_CONTENT_TYPE_STATUS_CODE = HTTP_UNSUPPORTED_TYPE;
    @NonNull
    private final PatchBookServiceFactory factory;

    public static String getNoPatchServiceForContentTypeResponseMessage(HttpExchange exchange, ContentType contentType) {
        return format("Server does not support content type [%s] for http method [%s] at the endpoint [%s].",
                contentType, PATCH, exchange.getHttpContext().getPath());
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

        val headers = exchange.getRequestHeaders();
        val contentTypeHeaderValue = headers.getFirst(CONTENT_TYPE.toString());

        ContentType contentType;
        try {
            contentType = getContentTypeFromString(contentTypeHeaderValue);
        } catch (UnsupportedContentTypeException e) {
            sendResponse(exchange, UNSUPPORTED_CONTENT_TYPE_STATUS_CODE, getUnsupportedContentTypeResponseMessage(contentTypeHeaderValue));
            return;
        }

        PatchBookService service;
        try {
            service = factory.getService(contentType);
        } catch (UnsupportedContentTypeException e) {
            sendResponse(exchange, NO_PATCH_SERVICE_FOR_CONTENT_TYPE_STATUS_CODE, getNoPatchServiceForContentTypeResponseMessage(exchange, contentType));
            return;
        }

        val attributes = exchange.getHttpContext().getAttributes();
        val uuidString = attributes.get(BOOK_ID.toString()).toString();
        val uuid = fromString(uuidString);
        service.patch(exchange, uuid);
    }
}
