package com.github.danielm94.server.handlers.book.httpmethod;

import com.github.danielm94.server.domain.book.serializer.BookSerializer;
import com.github.danielm94.server.domain.book.serializer.factory.BookSerializerFactory;
import com.github.danielm94.server.requestdata.content.ContentType;
import com.github.danielm94.server.requestdata.content.UnsupportedContentTypeException;
import com.github.danielm94.server.services.read.GetBookService;
import com.sun.net.httpserver.HttpExchange;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.val;

import java.util.UUID;

import static com.github.danielm94.server.exchange.Attributes.BOOK_ID;
import static com.github.danielm94.server.handlers.SimpleResponseHandler.sendResponse;
import static com.github.danielm94.server.requestdata.content.ContentType.getContentTypeFromString;
import static com.github.danielm94.server.requestdata.headers.HttpHeader.ACCEPT;
import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_NOT_ACCEPTABLE;

@AllArgsConstructor
public class GetBookHandler implements HttpMethodBookHandler {
    public static final int MISSING_ACCEPT_HEADER_STATUS_CODE = HTTP_BAD_REQUEST;
    public static final String MISSING_ACCEPT_HEADER_MESSAGE = "You must include the Accept header in your request.";
    public static final int UNSUPPORTED_CONTENT_TYPE_STATUS_CODE = HTTP_BAD_REQUEST;
    public static final int UNSUPPORTED_SERIALIZER_FORMAT_STATUS_CODE = HTTP_NOT_ACCEPTABLE;
    @NonNull
    private GetBookService bookService;
    @NonNull
    private BookSerializerFactory factory;

    public static String getUnsupportedContentTypeResponseBody(String acceptHeaderValue) {
        return acceptHeaderValue + " is not a supported content type.";
    }

    public static String getUnsupportedSerializerFormatResponseBody(ContentType contentType) {
        return "Server does not support formatting the books into " + contentType;
    }

    @Override
    public void handle(@NonNull HttpExchange exchange) {
        val headers = exchange.getRequestHeaders();
        val acceptHeaderValues = headers.get(ACCEPT.toString());

        if (acceptHeaderValues == null) {
            sendResponse(exchange, MISSING_ACCEPT_HEADER_STATUS_CODE, MISSING_ACCEPT_HEADER_MESSAGE);
            return;
        }

        val acceptHeaderValue = acceptHeaderValues.getFirst();

        ContentType contentType;
        try {
            contentType = getContentTypeFromString(acceptHeaderValue);
        } catch (UnsupportedContentTypeException e) {
            sendResponse(exchange, UNSUPPORTED_CONTENT_TYPE_STATUS_CODE, getUnsupportedContentTypeResponseBody(acceptHeaderValue));
            return;
        }

        BookSerializer serializer;
        try {
            serializer = factory.getSerializer(contentType);
        } catch (UnsupportedContentTypeException e) {
            sendResponse(exchange, UNSUPPORTED_SERIALIZER_FORMAT_STATUS_CODE, getUnsupportedSerializerFormatResponseBody(contentType));
            return;
        }

        val attributes = exchange.getHttpContext().getAttributes();
        val resourceId = (UUID) attributes.get(BOOK_ID.toString());

        if (resourceId == null) {
            bookService.getAll(exchange, serializer);
        } else {
            attributes.remove(BOOK_ID.toString());
            bookService.getById(exchange, serializer, resourceId);
        }
    }
}
