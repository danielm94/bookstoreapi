package com.github.danielm94.server.parsers.body;

import lombok.extern.flogger.Flogger;
import lombok.val;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

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

        val body = bodyBuilder.toString().trim();
        log.atFine().log("Parsed the following body from the request data - %s", body);

        return IOUtils.toInputStream(body, StandardCharsets.UTF_8);
    }
}
