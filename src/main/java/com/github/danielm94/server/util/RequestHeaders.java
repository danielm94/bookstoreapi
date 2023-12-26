package com.github.danielm94.server.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RequestHeaders {
    CONTENT_LENGTH("Content-Length");

    private final String headerKey;
    }
