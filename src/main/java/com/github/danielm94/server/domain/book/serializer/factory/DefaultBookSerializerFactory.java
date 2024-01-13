package com.github.danielm94.server.domain.book.serializer.factory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.danielm94.server.domain.book.serializer.BookSerializer;
import com.github.danielm94.server.domain.book.serializer.JsonBookSerializer;
import com.github.danielm94.server.requestdata.content.ContentType;
import com.github.danielm94.server.requestdata.content.UnsupportedContentTypeException;
import lombok.NonNull;
import lombok.val;

import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;
import static com.github.danielm94.server.requestdata.content.ContentType.*;

public class DefaultBookSerializerFactory implements BookSerializerFactory {
    @Override
    public BookSerializer getSerializer(@NonNull ContentType contentType) throws UnsupportedContentTypeException {
        return switch (contentType) {
            case ANY -> getSerializer(APPLICATION_JSON);
            case APPLICATION_JSON -> {
                val mapper = new ObjectMapper();
                mapper.registerModule(new JavaTimeModule());
                mapper.disable(WRITE_DATES_AS_TIMESTAMPS);

                yield new JsonBookSerializer(mapper);
            }
            default -> {
                var message = "Server does not support formatting response body into " + contentType;
                throw new UnsupportedContentTypeException(message);
            }
        };
    }
}
