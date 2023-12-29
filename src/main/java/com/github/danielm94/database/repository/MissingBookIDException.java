package com.github.danielm94.database.repository;

public class MissingBookIDException extends Exception {
    public MissingBookIDException(String message) {
        super(message);
    }
}
