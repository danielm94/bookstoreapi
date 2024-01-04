package com.github.danielm94.server.domain.book.serializer.factory;

import com.github.danielm94.server.domain.book.serializer.BookSerializer;
import com.github.danielm94.server.requestdata.content.ContentType;
import com.github.danielm94.server.requestdata.content.UnsupportedContentTypeException;

public interface BookSerializerFactory {
    BookSerializer getSerializer(ContentType contentType) throws UnsupportedContentTypeException;
}
