package com.github.danielm94.server.handlers.book.httpmethod;

import com.github.danielm94.server.requestdata.content.ContentType;
import com.github.danielm94.server.requestdata.content.UnsupportedContentTypeException;
import com.github.danielm94.server.services.create.CreateBookService;
import com.github.danielm94.server.services.create.factory.CreateBookServiceFactory;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import lombok.val;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import java.io.ByteArrayOutputStream;

import static com.github.danielm94.server.requestdata.content.ContentType.APPLICATION_JSON;
import static com.github.danielm94.server.requestdata.content.ContentType.TEXT_PLAIN;
import static com.github.danielm94.server.requestdata.headers.HttpHeader.CONTENT_LENGTH;
import static com.github.danielm94.server.requestdata.headers.HttpHeader.CONTENT_TYPE;
import static com.github.danielm94.server.requestdata.method.HttpMethod.PATCH;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

@SuppressWarnings("DataFlowIssue")
class PostBookHandlerUnitTest {
    private PostBookHandler handler;
    @Mock
    private CreateBookServiceFactory mockFactory;

    @Mock
    private CreateBookService mockService;
    @Mock
    private HttpExchange mockExchange;
    @Mock
    private HttpContext mockContext;

    private AutoCloseable autoCloseable;
    private Headers headers;

    @BeforeEach
    void setUp() throws UnsupportedContentTypeException {
        autoCloseable = openMocks(this);

        handler = new PostBookHandler(mockFactory);
        headers = new Headers();

        when(mockExchange.getHttpContext()).thenReturn(mockContext);
        when(mockExchange.getRequestMethod()).thenReturn(PATCH.toString());
        when(mockExchange.getResponseBody()).thenReturn(new ByteArrayOutputStream());
        when(mockExchange.getRequestHeaders()).thenReturn(headers);
        when(mockFactory.getService(any(ContentType.class))).thenReturn(mockService);
    }

    @AfterEach
    void tearDown() throws Exception {
        if (autoCloseable != null) autoCloseable.close();
    }

    @Test
    void nullPointerExceptionShouldBeThrownWhenPassingNullCreateBookServiceFactoryToConstructor() {
        assertThatThrownBy(() -> new PostBookHandler(null))
                .as("NullPointerException should be thrown if a null CreateBookServiceFactory is passed.")
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void nullPointerExceptionShouldBeThrownWhenPassingNullHttpExchangeToHandle() {
        assertThatThrownBy(() -> handler.handle(null))
                .as("NullPointerException should be thrown if a null HttpExchange is passed.")
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void handleShouldProcessValidHttpExchange() {
        headers.add(CONTENT_LENGTH.toString(), "1");
        headers.add(CONTENT_TYPE.toString(), APPLICATION_JSON.toString());

        handler.handle(mockExchange);

        val exchangeCaptor = ArgumentCaptor.forClass(HttpExchange.class);
        verify(mockService, times(1)).createBook(exchangeCaptor.capture());
        val capturedExchange = exchangeCaptor.getValue();

        assertThat(capturedExchange)
                .as("HttpExchange should be passed to service if it's valid.")
                .isEqualTo(mockExchange);
    }

    @Test
    void handleShouldNotProcessRequestIfItHasNoBody() {
        headers.add(CONTENT_LENGTH.toString(), "0");
        
        handler.handle(mockExchange);

        verify(mockService, never()).createBook(any(HttpExchange.class));
    }

    @Test
    void handleShouldNotProcessRequestIfItHasNoContentType() {
        headers.add(CONTENT_LENGTH.toString(), "100");

        handler.handle(mockExchange);

        verify(mockService, never()).createBook(any(HttpExchange.class));
    }

    @Test
    void handleShouldNotProcessRequestIfItHasAnUnsupportedContentType() {
        headers.add(CONTENT_LENGTH.toString(), "1");
        headers.add(CONTENT_TYPE.toString(), "definitely_not_real");

        handler.handle(mockExchange);

        assertThatCode(() -> handler.handle(mockExchange))
                .as("Handle method should deal with the UnsupportedContentTypeException when the exchange has no content type.")
                .doesNotThrowAnyException();
        
        verify(mockService, never()).createBook(any(HttpExchange.class));
    }

    @Test
    void handleShouldNotProcessRequestIfThereIsNoMatchingServiceForTheContentType() throws UnsupportedContentTypeException {
        headers.add(CONTENT_LENGTH.toString(), "100");
        headers.add(CONTENT_TYPE.toString(), TEXT_PLAIN.toString());

        when(mockFactory.getService(any(ContentType.class))).thenThrow(UnsupportedContentTypeException.class);

        assertThatCode(() -> handler.handle(mockExchange))
                .as("Handle method should deal with the UnsupportedContentTypeException when there is no service for the content type.")
                .doesNotThrowAnyException();

        verify(mockService, never()).createBook(any(HttpExchange.class));
    }
}