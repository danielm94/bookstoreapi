package com.github.danielm94.server.parsers.body;

import lombok.extern.flogger.Flogger;
import lombok.val;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Flogger
public class DefaultBodyParserStrategy implements BodyParserStrategy {

    @Override
    public InputStream parseBody(String[] requestArray) {
        log.atFine().log("Parsing request body from request data.");

        val bodyBuilder = new StringBuilder();
        var requestBodyReached = false;

        for (val line : requestArray) {
            if (requestBodyReached) bodyBuilder.append(line).append(System.lineSeparator());
            if (line.isBlank() || line.isEmpty()) requestBodyReached = true;
        }

        val body = bodyBuilder.toString();
        log.atFine().log("Parsed the following body from the request data - %s", body);

        return new ByteArrayInputStream(body.getBytes());
    }
}
