package com.github.danielm94.database.schemas;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DatabaseSchemas {
    BOOKSTOREAPI("bookstoreapi");

    private final String schemaName;

    @Override
    public String toString() {
        return schemaName;
    }
}
