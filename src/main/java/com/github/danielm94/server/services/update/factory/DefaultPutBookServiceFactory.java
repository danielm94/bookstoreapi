package com.github.danielm94.server.services.update.factory;

import com.github.danielm94.server.requestdata.content.ContentType;
import com.github.danielm94.server.requestdata.content.UnsupportedContentTypeException;
import com.github.danielm94.server.services.update.JsonPutBookService;
import com.github.danielm94.server.services.update.PutBookService;
import lombok.NonNull;
import lombok.val;

public class DefaultPutBookServiceFactory implements PutBookServiceFactory {
    @Override
    public PutBookService getPutBookService(@NonNull ContentType contentType) throws UnsupportedContentTypeException {
        switch (contentType) {
            case APPLICATION_JSON -> {
                return new JsonPutBookService();
            }
            default -> {
                val exceptionMessage = "Server currently doesn't support creating books using content type: " + contentType;
                throw new UnsupportedContentTypeException(exceptionMessage);
            }
        }
    }
}
