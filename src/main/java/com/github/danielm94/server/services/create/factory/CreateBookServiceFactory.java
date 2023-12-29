package com.github.danielm94.server.services.create.factory;

import com.github.danielm94.server.requestdata.content.ContentType;
import com.github.danielm94.server.requestdata.content.UnsupportedContentTypeException;
import com.github.danielm94.server.services.create.CreateBookService;
import lombok.NonNull;

public interface CreateBookServiceFactory {
    CreateBookService getServiceForContentType(@NonNull ContentType contentType) throws UnsupportedContentTypeException;
}
