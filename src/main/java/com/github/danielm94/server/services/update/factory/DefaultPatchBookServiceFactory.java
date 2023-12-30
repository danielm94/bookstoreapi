package com.github.danielm94.server.services.update.factory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.danielm94.server.domain.book.mappers.JsonBookDTOMapper;
import com.github.danielm94.server.requestdata.content.ContentType;
import com.github.danielm94.server.requestdata.content.UnsupportedContentTypeException;
import com.github.danielm94.server.services.update.JsonPatchBookService;
import com.github.danielm94.server.services.update.PatchBookService;
import lombok.NonNull;
import lombok.val;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

public class DefaultPatchBookServiceFactory implements PatchBookServiceFactory {
    @Override
    public PatchBookService getService(@NonNull ContentType contentType) throws UnsupportedContentTypeException {
        return switch (contentType) {
            case APPLICATION_JSON -> {
                val objectMapper = new ObjectMapper();
                objectMapper.configure(FAIL_ON_MISSING_CREATOR_PROPERTIES, false);
                objectMapper.configure(FAIL_ON_UNKNOWN_PROPERTIES, false);
                val dtoMapper = new JsonBookDTOMapper(objectMapper);
                yield new JsonPatchBookService(dtoMapper);
            }
            default -> {
                throw new UnsupportedContentTypeException("Server does not currently support patching books using the content type " + contentType);
            }
        };
    }
}
