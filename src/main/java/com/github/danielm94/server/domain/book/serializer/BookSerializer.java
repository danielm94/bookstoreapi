package com.github.danielm94.server.domain.book.serializer;

import com.github.danielm94.server.domain.book.Book;
import lombok.NonNull;

import java.util.List;

public interface BookSerializer {

    String serializeBook(@NonNull Book book) throws BookSerializationException;

    String serializeBooks(@NonNull List<Book> books) throws BookSerializationException;
}
