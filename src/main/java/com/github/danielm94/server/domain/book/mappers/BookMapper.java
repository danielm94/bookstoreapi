package com.github.danielm94.server.domain.book.mappers;

import com.github.danielm94.server.domain.book.Book;
import com.github.danielm94.server.domain.book.BookDTO;
import lombok.NonNull;
import lombok.extern.flogger.Flogger;
import lombok.val;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.github.danielm94.database.resultset.ResultSetParser.parseResultSetToListOfMaps;
import static com.github.danielm94.database.schemas.bookstoreapi.books.BooksColumn.*;
import static java.time.LocalDateTime.now;
import static java.time.LocalDateTime.parse;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;

@Flogger
public class BookMapper {
    private BookMapper() {
    }

    public static Book mapFromDTO(@NonNull BookDTO dto) {
        log.atFine().log("Mapping the following book dto into a book:\n%s", dto);

        val now = now();
        val book = Book.builder()
                       .id(UUID.randomUUID())
                       .bookName(dto.getBookName())
                       .author(dto.getAuthor())
                       .isbn(dto.getIsbn())
                       .price(dto.getPrice())
                       .dateAdded(now)
                       .dateUpdated(now)
                       .build();

        log.atFine().log("Book dto was successfully mapped to the following book:\n%s", book);
        return book;
    }

    public static List<Book> mapFromResultSet(@NonNull ResultSet resultSet) throws SQLException {
        log.atFine().log("Mapping result set into a list of books.");

        val books = new ArrayList<Book>();
        val table = parseResultSetToListOfMaps(resultSet);
        val formatter = ISO_LOCAL_DATE_TIME;
        for (val row : table) {
            val book = Book.builder()
                           .id(UUID.fromString(row.get(ID.toString()).toString()))
                           .bookName(row.get(BOOK_NAME.toString()).toString())
                           .author(row.get(AUTHOR.toString()).toString())
                           .isbn(row.get(ISBN.toString()).toString())
                           .price(new BigDecimal(row.get(PRICE.toString()).toString()))
                           .dateAdded(parse(row.get(DATE_ADDED.toString()).toString(), formatter))
                           .dateUpdated(parse(row.get(DATE_UPDATED.toString()).toString(), formatter))
                           .build();
            log.atFinest().log("Result set row was successfully mapped to the following book:\n%s", book);
            books.add(book);
        }

        log.atFine().log("Result set was successfully mapped to a list of books.");
        return books;
    }

}
