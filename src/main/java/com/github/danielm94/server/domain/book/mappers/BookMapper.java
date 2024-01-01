package com.github.danielm94.server.domain.book.mappers;

import com.github.danielm94.server.domain.book.Book;
import com.github.danielm94.server.domain.book.BookDTO;
import lombok.NonNull;
import lombok.extern.flogger.Flogger;
import lombok.val;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.github.danielm94.database.resultset.ResultSetParser.parseResultSetToListOfMaps;
import static com.github.danielm94.database.schemas.bookstoreapi.books.BooksColumn.*;
import static java.time.LocalDateTime.now;
import static java.time.LocalDateTime.parse;

@Flogger
public class BookMapper {
    private BookMapper() {
    }

    public static Book createNewBookFromDTO(@NonNull BookDTO dto) {
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

    public static List<Book> mapFromResultSet(@NonNull ResultSet resultSet, @NonNull DateTimeFormatter formatter) throws SQLException {
        log.atFine().log("Mapping result set into a list of books.");

        val books = new ArrayList<Book>();
        val table = parseResultSetToListOfMaps(resultSet);
        for (val row : table) {
            val idColumnKey = ID.toString().toUpperCase();
            val idColumnValue = row.get(idColumnKey).toString();
            val id = UUID.fromString(idColumnValue);

            val bookNameColumnKey = BOOK_NAME.toString().toUpperCase();
            val bookNameColumnValue = row.get(bookNameColumnKey).toString();

            val authorColumnKey = AUTHOR.toString().toUpperCase();
            val authorColumnValue = row.get(authorColumnKey).toString();

            val isbnColumnKey = ISBN.toString().toUpperCase();
            val isbnColumnValue = row.get(isbnColumnKey).toString();

            val priceColumnKey = PRICE.toString().toUpperCase();
            val priceColumnValue = new BigDecimal(row.get(priceColumnKey).toString());

            val dateAddedColumnKey = DATE_ADDED.toString().toUpperCase();
            val dateAddedColumnValue = parse(row.get(dateAddedColumnKey).toString(), formatter);

            val dateUpdatedColumnKey = DATE_UPDATED.toString().toUpperCase();
            val dateUpdatedColumnValue = parse(row.get(dateUpdatedColumnKey).toString(), formatter);

            val book = Book.builder()
                           .id(id)
                           .bookName(bookNameColumnValue)
                           .author(authorColumnValue)
                           .isbn(isbnColumnValue)
                           .price(priceColumnValue)
                           .dateAdded(dateAddedColumnValue)
                           .dateUpdated(dateUpdatedColumnValue)
                           .build();

            log.atFinest().log("Result set row was successfully mapped to the following book:\n%s", book);
            books.add(book);
        }

        log.atFine().log("Result set was successfully mapped to a list of books.");
        return books;
    }


}
