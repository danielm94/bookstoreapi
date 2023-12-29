package com.github.danielm94.server.handlers.book.httpmethod.factory;

import com.github.danielm94.server.handlers.book.httpmethod.GetBookHandler;
import com.github.danielm94.server.handlers.book.httpmethod.HttpMethodBookHandler;
import com.github.danielm94.server.handlers.book.httpmethod.PostBookHandler;
import com.github.danielm94.server.handlers.book.httpmethod.PutBookHandler;
import com.github.danielm94.server.requestdata.method.HttpMethod;
import com.github.danielm94.server.requestdata.method.UnsupportedHttpMethodException;
import lombok.NonNull;

public class DefaultHttpMethodBookHandlerFactory implements HttpMethodBookHandlerFactory {
    @Override
    public HttpMethodBookHandler getHandler(@NonNull HttpMethod method) throws UnsupportedHttpMethodException {
        return switch (method) {
            case GET -> new GetBookHandler();
            case PUT -> new PutBookHandler();
            case POST -> new PostBookHandler();
            default -> throw new UnsupportedHttpMethodException(method);
        };
    }
}
