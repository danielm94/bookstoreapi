package com.github.danielm94.server.requestdata.content;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ContentType {
    APPLICATION_JSON("application/json");

    private final String mimeType;

    @Override
    public String toString() {
        return mimeType;
    }
}
