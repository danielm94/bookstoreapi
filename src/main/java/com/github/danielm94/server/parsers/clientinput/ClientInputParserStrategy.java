package com.github.danielm94.server.parsers.clientinput;

import com.github.danielm94.server.util.io.StreamParsingException;

import java.io.InputStream;
import java.nio.charset.Charset;

public interface ClientInputParserStrategy {
    String parseInputStream(InputStream stream, Charset charSet) throws StreamParsingException;
}
