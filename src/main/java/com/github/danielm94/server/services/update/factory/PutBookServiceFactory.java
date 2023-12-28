package com.github.danielm94.server.services.update.factory;

import com.github.danielm94.server.requestdata.content.ContentType;
import com.github.danielm94.server.services.exceptions.UnsupportedContentTypeException;
import com.github.danielm94.server.services.update.PutBookService;
import lombok.NonNull;

public interface PutBookServiceFactory {
    PutBookService getPutBookService(@NonNull ContentType contentType) throws UnsupportedContentTypeException;
}
