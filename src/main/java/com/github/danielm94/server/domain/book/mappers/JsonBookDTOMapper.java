package com.github.danielm94.server.domain.book.mappers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.danielm94.server.domain.book.BookDTO;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.flogger.Flogger;
import lombok.val;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;


@Flogger
@AllArgsConstructor
public class JsonBookDTOMapper implements BookDTOMapper {
    @NonNull
    private final ObjectMapper objectMapper;


    @Override
    public BookDTO parseRequestBodyToBookDTO(@NonNull InputStream stream) throws IOException {
        val json = IOUtils.toString(stream, StandardCharsets.UTF_8);
        log.atFine().log("Parsed the following JSON from stream:\n%s", json);
        val bookDto = objectMapper.readValue(json, BookDTO.class);
        log.atFine().log("JSON was mapped to the following BookDTO:\n%s", bookDto);
        return bookDto;
    }
}
