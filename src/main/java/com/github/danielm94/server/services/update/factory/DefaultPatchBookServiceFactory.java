package com.github.danielm94.server.services.update.factory;

import com.github.danielm94.server.requestdata.content.ContentType;
import com.github.danielm94.server.requestdata.content.UnsupportedContentTypeException;
import com.github.danielm94.server.services.update.JsonPatchBookService;
import com.github.danielm94.server.services.update.PatchBookService;
import lombok.NonNull;

public class DefaultPatchBookServiceFactory implements PatchBookServiceFactory {
    @Override
    public PatchBookService getService(@NonNull ContentType contentType) throws UnsupportedContentTypeException {
        return switch (contentType) {
            case APPLICATION_JSON -> new JsonPatchBookService();
            default ->
                    throw new UnsupportedContentTypeException("Server does not currently support patching books using the content type " + contentType);
        };
    }
}
