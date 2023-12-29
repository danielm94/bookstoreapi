package com.github.danielm94.server.services.create.factory;

import com.github.danielm94.server.requestdata.content.ContentType;
import com.github.danielm94.server.requestdata.content.UnsupportedContentTypeException;
import com.github.danielm94.server.services.create.CreateBookService;
import com.github.danielm94.server.services.create.JsonCreateBookService;
import lombok.NonNull;
import lombok.val;

public class DefaultCreateBookServiceFactory implements CreateBookServiceFactory {
    @Override
    public CreateBookService getServiceForContentType(@NonNull ContentType contentType) throws UnsupportedContentTypeException {
        switch (contentType) {
            case APPLICATION_JSON -> {
                return new JsonCreateBookService();
            }
            default -> {
                val exceptionMessage = "Server currently doesn't support creating books using content type: " + contentType;
                throw new UnsupportedContentTypeException(exceptionMessage);
            }
        }
    }
}
