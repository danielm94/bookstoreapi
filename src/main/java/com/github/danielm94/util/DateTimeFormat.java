package com.github.danielm94.util;

import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

@AllArgsConstructor
public enum DateTimeFormat {
    H2_DATABASE_FORMAT("yyyy-MM-dd HH:mm:ss.S");

    @NonNull
    private final String format;

    @Override
    public String toString() {
        return format;
    }

    public DateTimeFormatter getFormatter() {
        return DateTimeFormatter.ofPattern(format);
    }

    public DateTimeFormatter getFormatter(@NonNull Locale locale) {
        return DateTimeFormatter.ofPattern(format, locale);
    }
}
