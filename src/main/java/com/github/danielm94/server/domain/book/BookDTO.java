package com.github.danielm94.server.domain.book;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BookDTO {
    private String bookName;
    private String author;
    private String isbn;
    private BigDecimal price;

    public boolean allFieldsAreNull() {
        return bookName == null && author == null && isbn == null && price == null;
    }
}
