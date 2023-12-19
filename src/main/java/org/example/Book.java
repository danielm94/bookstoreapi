package org.example;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class Book {

    private UUID id;
    private String bookName;
    private String author;
    private String isbn;
    private BigDecimal price;

}
