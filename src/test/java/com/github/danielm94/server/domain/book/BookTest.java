package com.github.danielm94.server.domain.book;

import lombok.val;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class BookTest {

    @Test
    void isEqualToDTONullBookTest() {
        val book = Book.builder()
                       .build();

        assertThat(book.isEqualToDTO(null))
                .as("isEqualToDTO should return false when BookDTO is null")
                .isFalse();
    }

    @Test
    void isEqualToDTOEqualBookTest() {
        val bookName = "Some Book";
        val author = "Some Author";
        val isbn = "Some ISBN";
        val price = new BigDecimal("1.99");
        val book = Book.builder()
                       .bookName(bookName)
                       .author(author)
                       .isbn(isbn)
                       .price(price)
                       .build();

        val dto = new BookDTO();
        dto.setBookName(bookName);
        dto.setAuthor(author);
        dto.setIsbn(isbn);
        dto.setPrice(price);

        assertThat(book.isEqualToDTO(dto))
                .as("isEqualToDTO should return true when BookDTO's fields are the same as the Book's fields.")
                .isTrue();
    }

    @Test
    void isEqualToDTOUnequalBookTest() {
        val book = Book.builder()
                       .bookName("Book 1")
                       .author("Author 1")
                       .isbn("Isbn 1")
                       .price(new BigDecimal("1.99"))
                       .build();

        val dto = new BookDTO();
        dto.setBookName("Book 2");
        dto.setAuthor("Author 2");
        dto.setIsbn("ISBN 2");
        dto.setPrice(new BigDecimal("2.99"));

        assertThat(book.isEqualToDTO(dto))
                .as("isEqualToDTO should return false when BookDTO's fields do not match the Book's fields.")
                .isFalse();
    }
}