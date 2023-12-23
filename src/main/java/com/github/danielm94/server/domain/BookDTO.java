package com.github.danielm94.server.domain;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BookDTO {
    private String bookName;
    private String author;
    private String isbn;
    private BigDecimal price;

}
