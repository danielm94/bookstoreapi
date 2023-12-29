package com.github.danielm94.server.domain.book;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import lombok.val;

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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateAdded;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateUpdated;

    public boolean isEqualToDTO(BookDTO dto) {
        if (dto == null) {
            return false;
        }

        val bookNameEquals = (bookName == null) ? dto.getBookName() == null : bookName.equals(dto.getBookName());
        val authorEquals = (author == null) ? dto.getAuthor() == null : author.equals(dto.getAuthor());
        val isbnEquals = (isbn == null) ? dto.getIsbn() == null : isbn.equals(dto.getIsbn());
        val priceEquals = (price == null) ? dto.getPrice() == null : price.compareTo(dto.getPrice()) == 0;

        return bookNameEquals && authorEquals && isbnEquals && priceEquals;
    }
}
