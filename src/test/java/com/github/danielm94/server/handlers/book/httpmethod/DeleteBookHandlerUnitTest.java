package com.github.danielm94.server.handlers.book.httpmethod;

import com.github.danielm94.server.services.delete.DeleteBookService;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import lombok.val;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.UUID;

import static com.github.danielm94.server.exchange.Attributes.BOOK_ID;
import static com.github.danielm94.server.requestdata.method.HttpMethod.DELETE;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

@SuppressWarnings("DataFlowIssue")
class DeleteBookHandlerUnitTest {
    @Mock
    private HttpContext mockContext;
    @Mock
    private HttpExchange mockExchange;
    @Mock
    private DeleteBookService mockDeleteService;
    private DeleteBookHandler deleteHandler;
    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = openMocks(this);
        deleteHandler = new DeleteBookHandler(mockDeleteService);
        when(mockExchange.getHttpContext()).thenReturn(mockContext);
        when(mockExchange.getRequestMethod()).thenReturn(DELETE.toString());
        when(mockExchange.getResponseBody()).thenReturn(new ByteArrayOutputStream());
    }

    @AfterEach
    void tearDown() throws Exception {
        if (autoCloseable != null) autoCloseable.close();
    }

    @Test
    void nullPointerExceptionShouldBeThrownWhenPassingNullDeleteBookServiceToConstructor() {
        assertThatThrownBy(() -> new DeleteBookHandler(null))
                .as("NullPointerException should be thrown if a null DeleteBookService is passed.")
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void nullPointerExceptionShouldBeThrownWhenPassingNullHttpExchangeToHandle() {
        assertThatThrownBy(() -> deleteHandler.handle(null))
                .as("NullPointerException should be thrown if a null HttpExchange is passed.")
                .isInstanceOf(NullPointerException.class);
    }


    @Test
    void handleMethodShouldUseDeleteServiceCorrectly() {
        val attributedMap = new HashMap<String, Object>();
        val resourceId = UUID.randomUUID();
        attributedMap.put(BOOK_ID.toString(), resourceId);
        when(mockExchange.getHttpContext().getAttributes()).thenReturn(attributedMap);

        val exchangeCaptor = ArgumentCaptor.forClass(HttpExchange.class);
        val idCaptor = ArgumentCaptor.forClass(UUID.class);

        deleteHandler.handle(mockExchange);
        verify(mockDeleteService).delete(exchangeCaptor.capture(), idCaptor.capture());

        val actualExchange = exchangeCaptor.getValue();
        assertThat(actualExchange)
                .as("DeleteBookService should call delete method with HttpExchange we passed into the handle method.")
                .isEqualTo(mockExchange);

        val actualId = idCaptor.getValue();
        assertThat(actualId)
                .as("DeleteBookService should call delete method with UUID in the exchange's attributes.")
                .isEqualTo(resourceId);
    }

    @Test
    void handleMethodShouldNotCallOnDeleteServiceIfIDIsMissing() {
        val emptyMap = new HashMap<String, Object>();
        when(mockExchange.getHttpContext().getAttributes()).thenReturn(emptyMap);

        deleteHandler.handle(mockExchange);

        verify(mockDeleteService, never()).delete(any(HttpExchange.class), any(UUID.class));
    }
}