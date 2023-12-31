package com.github.danielm94.server.domain.book.mappers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.danielm94.server.domain.book.BookDTO;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class JsonBookDTOMapperUnitTest {
    @Mock
    private ObjectMapper mapper;
    private JsonBookDTOMapper dtoMapper;
    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        this.autoCloseable = openMocks(this);
        this.dtoMapper = new JsonBookDTOMapper(mapper);
    }

    @Test
    void exceptionIsThrownWhenInstantiatingWithNullMapper() {
        assertThatThrownBy(() -> new JsonBookDTOMapper(null))
                .as("Exception is thrown when passing a null object mapper into constructor.")
                .isInstanceOf(NullPointerException.class);
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

        when(mapper.readValue(json, BookDTO.class)).thenReturn(expectedDto);

        val actualDto = dtoMapper.parseRequestBodyToBookDTO(stream);
        assertThat(actualDto)
                .as("Check if a valid JSON string is correctly parsed to a BookDTO object.")
                .isEqualTo(expectedDto);
    }

    @Test
    void exceptionIsThrownWhenPassingNullInputStream() {
        assertThatThrownBy(() -> dtoMapper.parseRequestBodyToBookDTO(null))
                .as("Exception should be thrown if a null input stream is passed as a parameter.")
                .isInstanceOf(NullPointerException.class);
    }
}