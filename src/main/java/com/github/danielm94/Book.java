package com.github.danielm94;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class Book {
    private final UUID id;
    private String bookName;
    private String author;
    private long isbn;
    private BigDecimal price;
    private LocalDateTime dateAdded;
    private LocalDateTime dateUpdated;
}
