package com.github.danielm94.server.domain.book.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.danielm94.server.domain.book.Book;
import lombok.val;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class JsonBookSerializerTest {
    @Mock
    private ObjectMapper objectMapper;
    private AutoCloseable autoCloseable;
    private JsonBookSerializer serializer;

    @BeforeEach
    void setUp() {
        this.autoCloseable = openMocks(this);
        this.serializer = new JsonBookSerializer(objectMapper);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void serializeBookReturnsAValidString() throws BookSerializationException, JsonProcessingException {
        val book = mock(Book.class);

        val expected = "some text";
        when(objectMapper.writeValueAsString(book)).thenReturn(expected);

        val actual = serializer.serializeBook(book);
        assertThat(actual)
                .as("Check if serializeBook returns the correct JSON string")
                .isEqualTo(expected);
    }

    @Test
    void serializeBookObjectMapperShouldSerializeBook() throws BookSerializationException, JsonProcessingException {
        val book = mock(Book.class);

        serializer.serializeBook(book);

        val bookArgumentCaptor = ArgumentCaptor.forClass(Book.class);
        verify(objectMapper).writeValueAsString(bookArgumentCaptor.capture());

        val capturedBook = bookArgumentCaptor.getValue();
        assertThat(capturedBook)
                .as("Check if ObjectMapper serializes the correct Book object")
                .isEqualTo(book);
    }

    @Test
    void serializeBookThrowsExceptionWhenBookIsNull() {
        assertThatThrownBy(() -> serializer.serializeBook(null))
                .as("Check if serializeBook throws NullPointerException for null Book")
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void serializeBookThrowsBookSerializationExceptionWhenFailingToSerialize() throws JsonProcessingException {
        val book = mock(Book.class);
        when(objectMapper.writeValueAsString(book)).thenThrow(JsonProcessingException.class);

        assertThatThrownBy(() -> serializer.serializeBook(book))
                .as("Check if serializeBook throws BookSerializationException when serialization fails")
                .isInstanceOf(BookSerializationException.class);
    }

    @Test
    void serializeBooksReturnsAValidString() throws BookSerializationException, JsonProcessingException {
        val book = mock(Book.class);
        val bookList = new ArrayList<Book>(1);
        bookList.add(book);

        val expected = "some text";
        when(objectMapper.writeValueAsString(bookList)).thenReturn(expected);

        val actual = serializer.serializeBooks(bookList);
        assertThat(actual)
                .as("Check if serializeBooks returns the correct JSON string for a list of books")
                .isEqualTo(expected);
    }

    @Test
    void serializeBooksObjectMapperShouldSerializeListOfBooks() throws BookSerializationException, JsonProcessingException {
        val book = mock(Book.class);
        val bookList = new ArrayList<Book>(1);
        bookList.add(book);

        serializer.serializeBooks(bookList);

        val bookListArgumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(objectMapper).writeValueAsString(bookListArgumentCaptor.capture());

        val capturedBookList = bookListArgumentCaptor.getValue();
        assertThat(capturedBookList)
                .as("Check if ObjectMapper serializes the correct list of Book objects")
                .isEqualTo(bookList);
    }

    @Test
    void serializeBooksThrowsExceptionWhenBookIsNull() {
        assertThatThrownBy(() -> serializer.serializeBooks(null))
                .as("Check if serializeBooks throws NullPointerException for null list of Books")
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void serializeBooksThrowsBookSerializationExceptionWhenFailingToSerialize() throws JsonProcessingException {
        val book = mock(Book.class);
        val bookList = new ArrayList<Book>(1);
        bookList.add(book);
        when(objectMapper.writeValueAsString(bookList)).thenThrow(JsonProcessingException.class);

        assertThatThrownBy(() -> serializer.serializeBooks(bookList))
                .as("Check if serializeBooks throws BookSerializationException when serialization fails")
                .isInstanceOf(BookSerializationException.class);
    }
}
