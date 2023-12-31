package com.github.danielm94.server.domain.book.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.danielm94.server.domain.book.Book;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static java.time.LocalDateTime.now;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class JsonBookSerializerIntegrationTest {
    private ObjectMapper objectMapper;
    private JsonBookSerializer serializer;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        serializer = new JsonBookSerializer(objectMapper);
    }

    @Test
    void serializeBookShouldReturnCorrectJson() throws Exception {
        val localDateTime = now().truncatedTo(SECONDS);

        val book = Book.builder()
                       .id(UUID.randomUUID())
                       .bookName("Test Book")
                       .author("Author Name")
                       .isbn("1234567890")
                       .price(new BigDecimal("29.99"))
                       .dateAdded(localDateTime)
                       .dateUpdated(localDateTime)
                       .build();

        val jsonResult = serializer.serializeBook(book);
        val deserializedBook = objectMapper.readValue(jsonResult, Book.class);

        assertThat(deserializedBook)
                .usingRecursiveComparison()
                .ignoringFields("dateAdded", "dateUpdated") // Ignore dateAdded and dateUpdated for direct comparison
                .isEqualTo(book);

        assertThat(deserializedBook.getDateAdded()).isEqualToIgnoringNanos(book.getDateAdded());
        assertThat(deserializedBook.getDateUpdated()).isEqualToIgnoringNanos(book.getDateUpdated());
    }

    @Test
    void serializeBooksShouldReturnCorrectJsonForListOfBooks() throws Exception {
        val localDateTime = now().truncatedTo(SECONDS);

        val book1 = Book.builder()
                        .id(UUID.randomUUID())
                        .bookName("Test Book 1")
                        .author("Author Name 1")
                        .isbn("1234567891")
                        .price(new BigDecimal("19.99"))
                        .dateAdded(localDateTime)
                        .dateUpdated(localDateTime)
                        .build();

        val book2 = Book.builder()
                        .id(UUID.randomUUID())
                        .bookName("Test Book 2")
                        .author("Author Name 2")
                        .isbn("1234567892")
                        .price(new BigDecimal("39.99"))
                        .dateAdded(localDateTime)
                        .dateUpdated(localDateTime)
                        .build();

        val bookList = List.of(book1, book2);

        val jsonResult = serializer.serializeBooks(bookList);
        val deserializedBookArray = objectMapper.readValue(jsonResult, Book[].class);
        val deserializedBookList = List.of(deserializedBookArray);

        assertThat(deserializedBookList).usingRecursiveComparison().isEqualTo(bookList);
    }

    @Test
    void serializeBookWithInvalidInputShouldThrowException() {
        val noTimeSupportObjectMapper = new ObjectMapper();
        val noTimeSupportSerializer = new JsonBookSerializer(noTimeSupportObjectMapper);

        val book = Book.builder()
                       .dateAdded(now())
                       .build();

        assertThatThrownBy(() -> noTimeSupportSerializer.serializeBook(book))
                .isInstanceOf(BookSerializationException.class)
                .hasCauseInstanceOf(JsonProcessingException.class);
    }
}
