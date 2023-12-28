package com.github.danielm94.server.domain.book.mappers;

import com.github.danielm94.server.domain.book.Book;
import com.github.danielm94.server.domain.book.BookDTO;
import lombok.extern.flogger.Flogger;
import lombok.val;

import java.time.LocalDateTime;
import java.util.UUID;

@Flogger
public class BookMapper {
    private BookMapper() {
    }

    public static Book mapFromDTO(BookDTO dto) {
        log.atFine().log("Mapping the following book dto into a book:\n%s", dto);
        val now = LocalDateTime.now();
        val book = Book.builder()
                       .id(UUID.randomUUID())
                       .bookName(dto.getBookName())
                       .author(dto.getBookName())
                       .isbn(dto.getIsbn())
                       .price(dto.getPrice())
                       .dateAdded(now)
                       .dateUpdated(now)
                       .build();
        log.atFine().log("Book dto was successfully mapped to the following book:\n%s", book);
        return book;
    }
}
