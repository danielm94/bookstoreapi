package com.github.danielm94.server.handlers.book;

import com.github.danielm94.server.handlers.book.httpmethod.HttpMethodBookHandler;
import com.github.danielm94.server.handlers.book.httpmethod.factory.HttpMethodBookHandlerFactory;
import com.github.danielm94.server.requestdata.method.HttpMethod;
import com.github.danielm94.server.requestdata.method.UnsupportedHttpMethodException;
import com.sun.net.httpserver.HttpExchange;
import lombok.val;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import java.io.ByteArrayOutputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

@SuppressWarnings("DataFlowIssue")
public class BookHandlerUnitTest {

    private BookHandler bookHandler;
    @Mock
    private HttpMethodBookHandlerFactory factory;
    @Mock
    private HttpExchange exchange;
    @Mock
    private HttpMethodBookHandler httpMethodBookHandler;
    private AutoCloseable autoCloseable;

    @BeforeEach
    public void setUp() {
        autoCloseable = openMocks(this);
        bookHandler = new BookHandler(factory);
        when(exchange.getResponseBody()).thenReturn(new ByteArrayOutputStream());
    }

    @AfterEach
    void tearDown() throws Exception {
        if (autoCloseable != null) autoCloseable.close();
    }

    @Test
    void nullPointerExceptionShouldBeThrownWhenPassingNullHttpMethodBookHandlerFactoryToConstructor() {
        assertThatThrownBy(() -> new BookHandler(null))
                .as("NullPointerException should be thrown if a null HttpMethodBookHandlerFactory is passed.")
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void nullPointerExceptionShouldBeThrownWhenPassingNullHttpExchangeToHandle() {
        assertThatThrownBy(() -> bookHandler.handle(null))
                .as("NullPointerException should be thrown if a null HttpExchange is passed.")
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void handleMethodShouldHandleHttpExchange() throws UnsupportedHttpMethodException {
        val method = HttpMethod.GET;
        when(exchange.getRequestMethod()).thenReturn(method.toString());
        when(factory.getHandler(any(HttpMethod.class))).thenReturn(httpMethodBookHandler);

        bookHandler.handle(exchange);

        val exchangeArgumentCaptor = ArgumentCaptor.forClass(HttpExchange.class);
        verify(httpMethodBookHandler).handle(exchangeArgumentCaptor.capture());

        val capturedExchange = exchangeArgumentCaptor.getValue();
        assertThat(capturedExchange)
                .as("Handle method should use an HttpMethodBookHandler to handle the HttpExchange")
                .isEqualTo(exchange);
    }

    @Test
    public void handleMethodShouldDealWithExceptionWhenHandlingAnExchangeWithUnsupportedHttpMethod() throws UnsupportedHttpMethodException {
        val unsupportedMethod = HttpMethod.OPTIONS;
        when(exchange.getRequestMethod()).thenReturn(unsupportedMethod.toString());
        when(factory.getHandler(any())).thenThrow(new UnsupportedHttpMethodException(unsupportedMethod));

        assertThatCode(() -> bookHandler.handle(exchange))
                .as("Handle method should deal with the UnsupportedHttpMethodException.")
                .doesNotThrowAnyException();
    }

    @Test
    public void handleMethodShouldNotProcessRequestFurtherWhenAnExchangeContainsUnsupportedHttpMethod() throws UnsupportedHttpMethodException {
        val unsupportedMethod = HttpMethod.OPTIONS;
        when(exchange.getRequestMethod()).thenReturn(unsupportedMethod.toString());
        when(factory.getHandler(any())).thenThrow(new UnsupportedHttpMethodException(unsupportedMethod));

        bookHandler.handle(exchange);
        verify(httpMethodBookHandler, never()).handle(any(HttpExchange.class));
    }
}
