package com.github.danielm94.server.parsers.headers;

import com.sun.net.httpserver.Headers;

import java.util.stream.Stream;


public interface HeaderParserStrategy {

    Headers parseHeaders(String[] headerArray);

}
