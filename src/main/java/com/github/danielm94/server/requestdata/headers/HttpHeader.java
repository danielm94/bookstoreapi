package com.github.danielm94.server.requestdata.headers;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum HttpHeader {
    CONTENT_LENGTH("Content-Length"),
    CONTENT_TYPE("Content-Type"),
    CONTENT_LOCATION("Content-Location:");

    private final String headerKey;

    @Override
    public String toString() {
        return headerKey;
    }
}
