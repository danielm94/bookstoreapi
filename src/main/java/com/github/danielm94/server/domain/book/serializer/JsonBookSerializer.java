package com.github.danielm94.server.domain.book.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.danielm94.server.domain.book.Book;
import lombok.NonNull;
import lombok.val;

import java.util.List;

import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;
import static java.lang.String.format;

public class JsonBookSerializer implements BookSerializer {
    private static String serialize(Object book, String exceptionMessage) throws BookSerializationException {
        val mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(WRITE_DATES_AS_TIMESTAMPS);
        try {
            return mapper.writeValueAsString(book);
        } catch (JsonProcessingException e) {
            throw new BookSerializationException(exceptionMessage, e);
        }
    }

    @Override
    public String serializeBook(@NonNull Book book) throws BookSerializationException {
        val exceptionMessage = format("Failed to serialize book into a JSON string. The book in question:\n%s", book);
        return serialize(book, exceptionMessage);
    }

    @Override
    public String serializeBooks(@NonNull List<Book> books) throws BookSerializationException {
        val booksStringBuilder = new StringBuilder();
        books.forEach(book -> booksStringBuilder.append(book.toString()).append(System.lineSeparator()));
        val exceptionMessage = format("Failed to serialize a list of %d books into a JSON string. " +
                "The books in question:\n%s", books.size(), booksStringBuilder);
        return serialize(books, exceptionMessage);
    }


}
