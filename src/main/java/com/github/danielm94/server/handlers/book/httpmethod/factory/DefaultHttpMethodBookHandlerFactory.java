package com.github.danielm94.server.handlers.book.httpmethod.factory;

import com.github.danielm94.server.handlers.book.httpmethod.*;
import com.github.danielm94.server.requestdata.method.HttpMethod;
import com.github.danielm94.server.requestdata.method.UnsupportedHttpMethodException;
import com.github.danielm94.server.services.delete.BookRemovalService;
import com.github.danielm94.server.services.read.BookRetrievalService;
import lombok.NonNull;

public class DefaultHttpMethodBookHandlerFactory implements HttpMethodBookHandlerFactory {
    @Override
    public HttpMethodBookHandler getHandler(@NonNull HttpMethod method) throws UnsupportedHttpMethodException {
        return switch (method) {
            case GET, HEAD -> new GetBookHandler(new BookRetrievalService());
            case PUT -> new PutBookHandler();
            case POST -> new PostBookHandler();
            case DELETE -> new DeleteBookHandler(new BookRemovalService());
            case PATCH -> new PatchBookHandler();
            default -> throw new UnsupportedHttpMethodException(method);
        };
    }
}
