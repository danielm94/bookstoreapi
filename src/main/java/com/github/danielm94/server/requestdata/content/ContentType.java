package com.github.danielm94.server.requestdata.content;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum ContentType {
    APPLICATION_JSON("application/json");
    @Getter(AccessLevel.NONE)
    private static final Map<String, ContentType> lookupMap;


    private final String mimeType;


    static {
        lookupMap = new HashMap<>();
        for (val value : values()) {
            lookupMap.put(value.getMimeType(), value);
        }
    }

    @Override
    public String toString() {
        return mimeType;
    }

    public static ContentType getContentTypeFromString(String mimeType) {
        return lookupMap.get(mimeType);
    }
}
