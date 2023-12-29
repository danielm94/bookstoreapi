package com.github.danielm94.server.requestdata.method;

public class UnsupportedHttpMethodException extends Exception {
    public UnsupportedHttpMethodException(HttpMethod method) {
        super(String.format("The HTTP method %s is not supported.", method));
    }
}
