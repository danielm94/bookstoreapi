package com.github.danielm94.server.domain.book.mappers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.danielm94.server.domain.book.BookDTO;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class JsonBookDTOMapperIntegrationTest {
    private ObjectMapper mapper;
    private JsonBookDTOMapper dtoMapper;

    @BeforeEach
    void setUp() {
        this.mapper = new ObjectMapper();
        this.dtoMapper = new JsonBookDTOMapper(mapper);
    }


    @Test
    public void parseRequestBodyShouldReturnValidBookDTO() throws IOException {
        val expectedDto = new BookDTO();
        expectedDto.setBookName("Test Book");
        expectedDto.setAuthor("Test Author");
        expectedDto.setIsbn("123456789");
        expectedDto.setPrice(new BigDecimal("19.99"));

        val objectMapper = new ObjectMapper();
        val json = objectMapper.writeValueAsString(expectedDto);
        val stream = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));

        val actualDto = dtoMapper.parseRequestBodyToBookDTO(stream);
        assertThat(actualDto)
                .as("Check if a valid JSON string is correctly parsed to a BookDTO object.")
                .isEqualTo(expectedDto);
    }

    @Test
    void exceptionIsThrownWhenPassingInvalidJSON() {
        val badJson = "this ain't json";
        val stream = new ByteArrayInputStream(badJson.getBytes(StandardCharsets.UTF_8));

        assertThatThrownBy(() -> dtoMapper.parseRequestBodyToBookDTO(stream))
                .as("Invalid JSON causes an exception to be thrown.")
                .isInstanceOf(IOException.class);
    }
}