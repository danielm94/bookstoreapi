package com.github.danielm94.server.exchange;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Attributes {
    BOOK_ID("bookId");

    private final String attribute;

    @Override
    public String toString() {
        return attribute;
    }
}
