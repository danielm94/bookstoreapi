package com.github.danielm94.server.handlers.book.httpmethod.factory;

import com.github.danielm94.server.handlers.book.httpmethod.HttpMethodBookHandler;
import com.github.danielm94.server.requestdata.method.HttpMethod;
import com.github.danielm94.server.requestdata.method.UnsupportedHttpMethodException;
import lombok.NonNull;

public interface HttpMethodBookHandlerFactory {
    HttpMethodBookHandler getHandler(@NonNull HttpMethod method) throws UnsupportedHttpMethodException;
}
