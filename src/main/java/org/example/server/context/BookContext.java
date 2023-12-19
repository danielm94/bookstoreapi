package org.example.server.context;

import com.sun.net.httpserver.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookContext extends HttpContext {
    private final HttpServer server;
    private final String path;

    private final Map<String, Object> attributes;
    private final List<Filter> filters;
    private HttpHandler handler;

    private Authenticator authenticator;


    public BookContext(HttpServer server, String path) {
        this.server = server;
        this.path = path;
        this.filters = new ArrayList<>();
        this.attributes = new HashMap<>();
    }

    @Override
    public HttpHandler getHandler() {
        return handler;
    }

    @Override
    public void setHandler(HttpHandler handler) {
        this.handler = handler;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public HttpServer getServer() {
        return server;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public List<Filter> getFilters() {
        return filters;
    }

    @Override
    public Authenticator setAuthenticator(Authenticator auth) {
        return this.authenticator = auth;
    }

    @Override
    public Authenticator getAuthenticator() {
        return authenticator;
    }
}
