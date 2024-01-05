package com.github.danielm94.server.handlers.book.httpmethod;

import com.github.danielm94.server.requestdata.content.ContentType;
import com.github.danielm94.server.requestdata.content.UnsupportedContentTypeException;
import com.github.danielm94.server.services.update.PatchBookService;
import com.github.danielm94.server.services.update.factory.PatchBookServiceFactory;
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
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.github.danielm94.server.exchange.Attributes.BOOK_ID;
import static com.github.danielm94.server.requestdata.content.ContentType.APPLICATION_JSON;
import static com.github.danielm94.server.requestdata.content.ContentType.TEXT_PLAIN;
import static com.github.danielm94.server.requestdata.headers.HttpHeader.CONTENT_LENGTH;
import static com.github.danielm94.server.requestdata.headers.HttpHeader.CONTENT_TYPE;
import static com.github.danielm94.server.requestdata.method.HttpMethod.PATCH;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

@SuppressWarnings("DataFlowIssue")
class PatchBookHandlerUnitTest {
    private Map<String, Object> attributeMap;
    @Mock
    private PatchBookServiceFactory mockFactory;

    @Mock
    private HttpContext mockContext;
    @Mock
    private PatchBookService mockService;
    @Mock
    private HttpExchange mockExchange;

    private PatchBookHandler handler;
    private AutoCloseable autoCloseable;
    private Headers headers;

    @BeforeEach
    void setUp() throws UnsupportedContentTypeException {
        autoCloseable = openMocks(this);

        handler = new PatchBookHandler(mockFactory);
        attributeMap = new HashMap<>();
        headers = new Headers();

        when(mockExchange.getHttpContext()).thenReturn(mockContext);
        when(mockExchange.getRequestMethod()).thenReturn(PATCH.toString());
        when(mockExchange.getResponseBody()).thenReturn(new ByteArrayOutputStream());
        when(mockContext.getAttributes()).thenReturn(attributeMap);
        when(mockExchange.getRequestHeaders()).thenReturn(headers);
        when(mockFactory.getService(any(ContentType.class))).thenReturn(mockService);
    }

    @AfterEach
    void tearDown() throws Exception {
        if (autoCloseable != null) autoCloseable.close();
    }

    @Test
    void nullPointerExceptionShouldBeThrownWhenPassingNullPatchBookServiceFactoryToConstructor() {
        assertThatThrownBy(() -> new PatchBookHandler(null))
                .as("NullPointerException should be thrown if a null PatchBookServiceFactory is passed.")
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void nullPointerExceptionShouldBeThrownWhenPassingNullHttpExchangeToHandler() {
        assertThatThrownBy(() -> handler.handle(null))
                .as("NullPointerException should be thrown if a null HttpExchange is passed.")
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void handleMethodShouldPassExchangeAndUUIDToServiceIfTheyAreValid() {
        val uuid = UUID.randomUUID();
        attributeMap.put(BOOK_ID.toString(), uuid);

        headers.add(CONTENT_TYPE.toString(), APPLICATION_JSON.toString());
        headers.add(CONTENT_LENGTH.toString(), "100");

        val exchangeCaptor = ArgumentCaptor.forClass(HttpExchange.class);
        val uuidCaptor = ArgumentCaptor.forClass(UUID.class);

        handler.handle(mockExchange);

        verify(mockService, times(1)).patch(exchangeCaptor.capture(), uuidCaptor.capture());

        val capturedExchange = exchangeCaptor.getValue();
        assertThat(capturedExchange)
                .as("Verify that exchange is passed to the patch book service if it is valid.")
                .isEqualTo(mockExchange);

        val capturedUUID = uuidCaptor.getValue();
        assertThat(capturedUUID)
                .as("Verify that UUID is passed to the patch book service if it is valid.")
                .isEqualTo(uuid);
    }

    @Test
    void handleShouldNotProcessRequestIfExchangeIsMissingUUID() {
        handler.handle(mockExchange);

        verify(mockService, never()).patch(any(HttpExchange.class), any(UUID.class));
    }

    @Test
    void handleShouldNotProcessRequestIfExchangeIsMissingRequestBody() {
        val uuid = UUID.randomUUID();
        attributeMap.put(BOOK_ID.toString(), uuid);

        handler.handle(mockExchange);

        verify(mockService, never()).patch(any(HttpExchange.class), any(UUID.class));
    }

    @Test
    void handleShouldNotProcessRequestIfExchangeHasNoContentType() {
        val uuid = UUID.randomUUID();
        attributeMap.put(BOOK_ID.toString(), uuid);

        headers.add(CONTENT_LENGTH.toString(), "100");

        assertThatCode(() -> handler.handle(mockExchange))
                .as("Handle method should deal with the UnsupportedContentTypeException when the exchange has no content type.")
                .doesNotThrowAnyException();

        verify(mockService, never()).patch(any(HttpExchange.class), any(UUID.class));
    }

    @Test
    void handleShouldNotProcessRequestIfExchangeHasUnsupportedContentType() throws UnsupportedContentTypeException {
        val uuid = UUID.randomUUID();
        attributeMap.put(BOOK_ID.toString(), uuid);

        headers.add(CONTENT_LENGTH.toString(), "100");
        headers.add(CONTENT_TYPE.toString(), TEXT_PLAIN.toString());

        when(mockFactory.getService(any(ContentType.class))).thenThrow(UnsupportedContentTypeException.class);

        assertThatCode(() -> handler.handle(mockExchange))
                .as("Handle method should deal with the UnsupportedContentTypeException when there is no service for the content type.")
                .doesNotThrowAnyException();

        verify(mockService, never()).patch(any(HttpExchange.class), any(UUID.class));
    }
}