package com.github.danielm94.server.handlers.book.httpmethod;

import com.github.danielm94.server.domain.book.serializer.BookSerializer;
import com.github.danielm94.server.domain.book.serializer.factory.BookSerializerFactory;
import com.github.danielm94.server.requestdata.content.ContentType;
import com.github.danielm94.server.requestdata.content.UnsupportedContentTypeException;
import com.github.danielm94.server.services.read.GetBookService;
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
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import static com.github.danielm94.server.exchange.Attributes.BOOK_ID;
import static com.github.danielm94.server.requestdata.content.ContentType.TEXT_PLAIN;
import static com.github.danielm94.server.requestdata.headers.HttpHeader.ACCEPT;
import static com.github.danielm94.server.requestdata.method.HttpMethod.GET;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

@SuppressWarnings("DataFlowIssue")
class GetBookHandlerUnitTest {
    public static final List<String> ACCEPT_HEADER = List.of("*/*");
    @Mock
    private GetBookService mockService;

    @Mock
    private BookSerializerFactory mockBookSerializerFactory;
    @Mock
    private HttpExchange mockExchange;
    @Mock
    private HttpContext mockContext;
    @Mock
    private Headers mockHeaders;
    private GetBookHandler getBookHandler;
    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = openMocks(this);
        getBookHandler = new GetBookHandler(mockService, mockBookSerializerFactory);

        when(mockExchange.getHttpContext()).thenReturn(mockContext);
        when(mockExchange.getRequestMethod()).thenReturn(GET.toString());
        when(mockExchange.getRequestHeaders()).thenReturn(mockHeaders);
        when(mockExchange.getResponseBody()).thenReturn(new ByteArrayOutputStream());
        when(mockHeaders.get(anyString())).thenReturn(ACCEPT_HEADER);
    }

    @AfterEach
    void tearDown() throws Exception {
        if (autoCloseable != null) autoCloseable.close();
    }

    @Test
    void nullPointerExceptionShouldBeThrownWhenPassingNullGetBookServiceToConstructor() {
        assertThatThrownBy(() -> new GetBookHandler(null, mockBookSerializerFactory))
                .as("NullPointerException should be thrown if a null GetBookService is passed.")
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void nullPointerExceptionShouldBeThrownWhenPassingNullBookSerializerFactoryToConstructor() {
        assertThatThrownBy(() -> new GetBookHandler(mockService, null))
                .as("NullPointerException should be thrown if a null BookSerializerFactory is passed.")
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void nullPointerExceptionShouldBeThrownWhenPassingNullHttpExchangeToHandleMethod() {
        assertThatThrownBy(() -> getBookHandler.handle(null))
                .as("NullPointerException should be thrown if a null HttpExchange is passed.")
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void handleMethodShouldGetAllBooksIfNoUUIDSpecified() {
        val emptyAttributesMap = new HashMap<String, Object>();
        when(mockContext.getAttributes()).thenReturn(emptyAttributesMap);

        getBookHandler.handle(mockExchange);

        verify(mockService, times(1)).getAll(any(), any());
    }

    @Test
    void handleMethodShouldGetBookByIdIfUUIDSpecified() {
        val attributesMap = new HashMap<String, Object>();
        attributesMap.put(BOOK_ID.toString(), randomUUID());
        when(mockContext.getAttributes()).thenReturn(attributesMap);

        getBookHandler.handle(mockExchange);

        verify(mockService, times(1)).getById(any(), any(), any());
    }

    @Test
    void handleMethodShouldGetAcceptHeadersFromClient() {
        getBookHandler.handle(mockExchange);

        verify(mockHeaders, times(1)).get(ACCEPT.toString());
    }

    @Test
    void handleMethodShouldGetBookSerializerFromFactory() throws UnsupportedContentTypeException {
        getBookHandler.handle(mockExchange);

        verify(mockBookSerializerFactory, times(1)).getSerializer(any());
    }

    @Test
    void handleMethodShouldPassSerializerFromFactoryToServiceNoId() throws UnsupportedContentTypeException {
        val attributesMap = new HashMap<String, Object>();
        attributesMap.put(BOOK_ID.toString(), randomUUID());
        when(mockContext.getAttributes()).thenReturn(attributesMap);

        val mockSerializer = mock(BookSerializer.class);
        val serializerCaptor = ArgumentCaptor.forClass(BookSerializer.class);

        when(mockBookSerializerFactory.getSerializer(any())).thenReturn(mockSerializer);

        getBookHandler.handle(mockExchange);

        verify(mockService, times(1)).getById(any(HttpExchange.class), serializerCaptor.capture(), any(UUID.class));

        val capturedSerializer = serializerCaptor.getValue();
        assertThat(capturedSerializer)
                .as("Handle method should pass BookSerializer from the factory class to the get book by id service.")
                .isEqualTo(mockSerializer);

    }

    @Test
    void handleMethodShouldPassSerializerFromFactoryToServiceById() throws UnsupportedContentTypeException {
        val mockSerializer = mock(BookSerializer.class);
        val serializerCaptor = ArgumentCaptor.forClass(BookSerializer.class);

        when(mockBookSerializerFactory.getSerializer(any())).thenReturn(mockSerializer);

        getBookHandler.handle(mockExchange);

        verify(mockService, times(1)).getAll(any(HttpExchange.class), serializerCaptor.capture());

        val capturedSerializer = serializerCaptor.getValue();
        assertThat(capturedSerializer)
                .as("Handle method should pass BookSerializer from the factory class to the get all books service.")
                .isEqualTo(mockSerializer);

    }

    @Test
    void handleMethodShouldDealWithUnsupportedContentTypeFromAcceptHeader() {
        val unsupportedContentType = "Aaskdjhakdhaskj";
        val acceptHeaderList = new LinkedList<String>();
        acceptHeaderList.add(unsupportedContentType);
        when(mockHeaders.get(ACCEPT.toString())).thenReturn(acceptHeaderList);

        assertThatCode(() -> getBookHandler.handle(mockExchange))
                .as("Handle method should deal with the UnsupportedContentTypeException")
                .doesNotThrowAnyException();
    }

    @Test
    void handleMethodShouldDealWithUnsupportedContentTypeFromAcceptHeaderWhenSearchingForSerializer() throws UnsupportedContentTypeException {
        val acceptHeaderList = new LinkedList<String>();
        acceptHeaderList.add(TEXT_PLAIN.toString());
        when(mockHeaders.get(ACCEPT.toString())).thenReturn(acceptHeaderList);
        when(mockBookSerializerFactory.getSerializer(any(ContentType.class))).thenThrow(UnsupportedContentTypeException.class);

        assertThatCode(() -> getBookHandler.handle(mockExchange))
                .as("Handle method should deal with the UnsupportedContentTypeException")
                .doesNotThrowAnyException();
    }
}