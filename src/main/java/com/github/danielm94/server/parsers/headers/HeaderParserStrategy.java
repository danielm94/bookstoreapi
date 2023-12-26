package com.github.danielm94.server.parsers.headers;

import com.sun.net.httpserver.Headers;


public interface HeaderParserStrategy {

    Headers parseHeaders(String[] headerArray);

}
