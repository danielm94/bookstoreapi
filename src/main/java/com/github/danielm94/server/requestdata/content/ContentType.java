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
    APPLICATION_JSON("application/json"),
    TEXT_PLAIN("text/plain");
    @Getter(AccessLevel.NONE)
    private static final Map<String, ContentType> lookupMap;

    static {
        lookupMap = new HashMap<>();
        for (val value : values()) {
            lookupMap.put(value.getMimeType(), value);
        }
    }

    private final String mimeType;

    public static ContentType getContentTypeFromString(String mimeType) throws UnsupportedContentTypeException {
        val contentType = lookupMap.get(mimeType);
        if (contentType == null) {
            throw new UnsupportedContentTypeException(mimeType + " is not a supported content type.");
        }
        return lookupMap.get(mimeType);
    }


    @Override
    public String toString() {
        return mimeType;
    }
}
