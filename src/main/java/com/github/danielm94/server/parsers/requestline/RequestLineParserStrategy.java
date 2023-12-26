package com.github.danielm94.server.parsers.requestline;

public interface RequestLineParserStrategy {
    RequestLine parseRequestLine(String requestLine);
}
