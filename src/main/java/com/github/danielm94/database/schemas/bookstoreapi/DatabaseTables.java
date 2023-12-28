package com.github.danielm94.database.schemas.bookstoreapi;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum DatabaseTables {
    BOOKS("books");

    private final String tableName;

    @Override
    public String toString() {
        return tableName;
    }
}
