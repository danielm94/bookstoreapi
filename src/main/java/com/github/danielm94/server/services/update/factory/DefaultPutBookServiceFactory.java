package com.github.danielm94.server.services.update.factory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.danielm94.server.domain.book.mappers.JsonBookDTOMapper;
import com.github.danielm94.server.requestdata.content.ContentType;
import com.github.danielm94.server.requestdata.content.UnsupportedContentTypeException;
import com.github.danielm94.server.services.update.JsonPutBookService;
import com.github.danielm94.server.services.update.PutBookService;
import lombok.NonNull;
import lombok.val;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

public class DefaultPutBookServiceFactory implements PutBookServiceFactory {
    @Override
    public PutBookService getPutBookService(@NonNull ContentType contentType) throws UnsupportedContentTypeException {
        return switch (contentType) {
            case APPLICATION_JSON -> {
                val objectMapper = new ObjectMapper();
                objectMapper.configure(FAIL_ON_UNKNOWN_PROPERTIES, false);
                val dtoMapper = new JsonBookDTOMapper(objectMapper);
                yield new JsonPutBookService(dtoMapper);
            }
            default -> {
                val exceptionMessage = "Server currently doesn't support creating books using content type: " + contentType;
                throw new UnsupportedContentTypeException(exceptionMessage);
            }
        };
    }
}
