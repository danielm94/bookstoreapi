package com.github.danielm94.server.parsers.body;

import java.io.InputStream;

public interface BodyParserStrategy {
    InputStream parseBody(String[] requestArray);
}
