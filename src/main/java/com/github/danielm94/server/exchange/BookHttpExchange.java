package com.github.danielm94.server.exchange;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpPrincipal;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.val;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static com.github.danielm94.server.requestdata.headers.HttpHeader.*;

@Getter
@Setter
public class BookHttpExchange extends HttpExchange {
    private final Map<String, Object> attributes = new HashMap<>();
    private String requestMethod;
    private URI requestURI;
    private Headers requestHeaders = new Headers();
    private Headers responseHeaders = new Headers();
    private HttpContext httpContext;
    private int responseCode;
    private InetSocketAddress remoteAddress;
    private InetSocketAddress localAddress;
    private String protocol;
    private InputStream requestBody;
    private OutputStream responseBody;
    private HttpPrincipal principal;

    private void addContentLengthHeader(long responseLength) {
        responseLength = Math.max(responseLength, 0);
        val key = CONTENT_LENGTH.toString();
        val value = Long.toString(responseLength);
        responseHeaders.add(key, value);
    }

    @Override
    public Headers getRequestHeaders() {
        return requestHeaders;
    }

    @Override
    public Headers getResponseHeaders() {
        return responseHeaders;
    }

    @Override
    public URI getRequestURI() {
        return requestURI;
    }

    @Override
    public String getRequestMethod() {
        return requestMethod;
    }

    @Override
    public HttpContext getHttpContext() {
        return httpContext;
    }

    @SneakyThrows(IOException.class)
    @Override
    public void close() {
        if (requestBody != null) requestBody.close();
        if (responseBody != null) responseBody.close();
    }

    @Override
    public InputStream getRequestBody() {
        return requestBody;
    }

    @Override
    public OutputStream getResponseBody() {
        return responseBody;
    }

    @Override
    public void sendResponseHeaders(int rCode, long responseLength) throws IOException {
        this.responseCode = rCode;
        addContentLengthHeader(responseLength);

        val headerBuilder = new StringBuilder();
        headerBuilder.append(protocol).append(" ").append(rCode).append(System.lineSeparator());

        for (val entry : responseHeaders.entrySet()) {
            for (val value : entry.getValue()) {
                headerBuilder.append(entry.getKey()).append(": ").append(value).append(System.lineSeparator());
            }
        }

        headerBuilder.append(System.lineSeparator());

        if (responseBody != null) {
            responseBody.write(headerBuilder.toString().getBytes(StandardCharsets.UTF_8));
            responseBody.flush();
        }
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return remoteAddress;
    }

    @Override
    public int getResponseCode() {
        return responseCode;
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return localAddress;
    }

    @Override
    public String getProtocol() {
        return protocol;
    }

    @Override
    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    @Override
    public void setAttribute(String name, Object value) {
        attributes.put(name, value);
    }

    @Override
    public void setStreams(InputStream i, OutputStream o) {
        this.requestBody = i;
        this.responseBody = o;
    }

    @Override
    public HttpPrincipal getPrincipal() {
        return principal;
    }
}
