package com.github.danielm94.server.domain.book.mappers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
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
                .isInstanceOf(NullPointerException.class);
    }


}