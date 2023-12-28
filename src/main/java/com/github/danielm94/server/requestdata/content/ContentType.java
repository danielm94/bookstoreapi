package com.github.danielm94.server.requestdata.content;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString(of = "mimeType")
public enum ContentType {
    APPLICATION_JSON("application/json");

    private final String mimeType;
}
