package org.example.server.context;

import com.sun.net.httpserver.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Setter
@Getter
public class BookContext extends HttpContext {
    private final Map<String, Object> attributes = new HashMap<>();
    private final List<Filter> filters = new ArrayList<>();
    private HttpServer server;
    private String path;
    private HttpHandler handler;
    private Authenticator authenticator;


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
