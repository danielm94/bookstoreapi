package com.github.danielm94.server.parsers.requestline;

public class RequestLineParsingException extends Exception {
    public RequestLineParsingException(String message) {
        super(message);
    }

    public RequestLineParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}
