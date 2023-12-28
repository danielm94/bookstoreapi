package com.github.danielm94.server.domain.book;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@Jacksonized
public class Book {
    private final UUID id;
    private String bookName;
    private String author;
    private String isbn;
    private BigDecimal price;
    private LocalDateTime dateAdded;
    private LocalDateTime dateUpdated;
}
