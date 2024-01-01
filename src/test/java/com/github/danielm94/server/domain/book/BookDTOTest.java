package com.github.danielm94.server.domain.book;

import lombok.val;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class BookDTOTest {

    @Test
    void allFieldsAreNullAllNullFields() {
        assertThat(new BookDTO().allFieldsAreNull())
                .as("allFieldsAreNull should return true if all fields are null.")
                .isTrue();
    }

    @Test
    void allFieldsAreNullSomeNullFields() {
        val dto = new BookDTO();
        dto.setAuthor("Some Author");
        dto.setBookName("Some Book");

        assertThat(dto.allFieldsAreNull())
                .as("allFieldsAreNull should return false if some fields are null.")
                .isFalse();
    }

    @Test
    void allFieldsAreNullNoNullFields() {
        val dto = new BookDTO();
        dto.setAuthor("Some Author");
        dto.setBookName("Some Book");
        dto.setIsbn("Some ISBN");
        dto.setPrice(new BigDecimal("1.99"));

        assertThat(dto.allFieldsAreNull())
                .as("allFieldsAreNull should return false if no fields are null.")
                .isFalse();
    }
}