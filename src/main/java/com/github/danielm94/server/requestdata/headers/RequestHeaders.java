package com.github.danielm94.server.requestdata.headers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString(of = "headerKey")
public enum RequestHeaders {
    CONTENT_LENGTH("Content-Length"),
    CONTENT_TYPE("Content-Type");

    private final String headerKey;
}
