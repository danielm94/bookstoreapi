package com.github.danielm94.server.parsers.headers;

import com.sun.net.httpserver.Headers;
import lombok.extern.flogger.Flogger;
import lombok.val;

@Flogger
public class DefaultHeaderParserStrategy implements HeaderParserStrategy {

    @Override
    public Headers parseHeaders(String[] headerArray) {
        log.atFine().log("Parsing headers from request data.");
        val headers = new Headers();
        for (val line : headerArray) {
            val endOfHeadersReached = line.isBlank() || line.isEmpty();
            if (endOfHeadersReached) break;
            var keyValueArray = line.split(":");
            if (lineIsAHeader(keyValueArray)) {
                val key = keyValueArray[0];
                val value = keyValueArray[1];
                headers.add(key, value);
                log.atFinest().log("Adding header with key:%s|value:%s", key, value);
            }
        }
        log.atFine().log("Parsed %d headers from request data.", headers.size());
        return headers;
    }

    private static boolean lineIsAHeader(String[] keyValueArray) {
        return keyValueArray.length == 2;
    }
}
