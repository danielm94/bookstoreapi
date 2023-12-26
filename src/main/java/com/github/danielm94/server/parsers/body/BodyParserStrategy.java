package com.github.danielm94.server.parsers.body;

import java.io.InputStream;
import java.util.stream.Stream;

public interface BodyParserStrategy {
    InputStream parseBody(String[] requestArray);
}
