package com.github.danielm94.server.util.io;

public class StreamParsingException extends Exception {
    public StreamParsingException(String message) {
        super(message);
    }

    public StreamParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}
