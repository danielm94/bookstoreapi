package org.example.server.exchange;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpPrincipal;
import lombok.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class BookHttpExchange extends HttpExchange {
    private final Map<String, Object> attributes = new HashMap<>();
    private String requestMethod;
    private URI requestURI;
    private Headers requestHeaders;
    private Headers responseHeaders;
    private HttpContext httpContext;
    private int responseCode;
    private InetSocketAddress remoteAddress;
    private InetSocketAddress localAddress;
    private String protocol;
    private InputStream requestBody;
    private OutputStream responseBody;
    private HttpPrincipal principal;

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
    @SneakyThrows(IOException.class)
    public void sendResponseHeaders(int rCode, long responseLength) {

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

    }

    @Override
    public HttpPrincipal getPrincipal() {
        return principal;
    }
}
