package com.github.danielm94.database.schemas;

import lombok.Getter;

@Getter
public enum DatabaseSchemas {
    BOOKSTOREAPI("bookstoreapi");

    private final String schemaName;

    DatabaseSchemas(String schemaName) {
        this.schemaName = schemaName;
    }

}
