package com.github.danielm94.server.domain.book.parsers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.danielm94.server.domain.book.BookDTO;
import lombok.NonNull;
import lombok.val;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class JsonBookDTOParser implements BookDTOParser {
    @Override
    public BookDTO parseRequestBodyToBookDTO(@NonNull InputStream stream) throws IOException {
        val json = IOUtils.toString(stream, StandardCharsets.UTF_8);
        val objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, BookDTO.class);
    }
}
