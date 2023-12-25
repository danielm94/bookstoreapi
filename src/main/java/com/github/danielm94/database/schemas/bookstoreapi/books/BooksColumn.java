package com.github.danielm94.database.schemas.bookstoreapi.books;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BooksColumn {
    ID("id"),
    BOOK_NAME("bookName"),
    AUTHOR("author"),
    ISBN("isbn"),
    PRICE("price"),
    DATE_ADDED("dateAdded"),
    DATE_UPDATED("dateUpdated");

    private final String columnName;
}
