package com.github.danielm94.server.parsers.clientinput;

import com.github.danielm94.server.util.RequestHeaders;
import lombok.extern.flogger.Flogger;
import lombok.val;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

@Flogger
public class DefaultClientInputParserStrategy implements ClientInputParserStrategy {
    @Override
    public String parseInputStream(InputStream stream, Charset charSet) throws StreamParsingException {
        if (stream == null) {
            throw new StreamParsingException("Could not parse InputStream into a String as it was null.");
        }

        val stringBuilder = new StringBuilder();
        var contentLengthFound = false;
        val contentLengthHeaderKey = RequestHeaders.CONTENT_LENGTH.toString();
        var contentLength = 0;
        val inputStreamReader = new InputStreamReader(stream, charSet);
        val bufferedReader = new BufferedReader(inputStreamReader);


        String line;
        while (true) {
            try {
                if ((line = bufferedReader.readLine()) == null) break;
            } catch (IOException e) {
                val message = stringBuilder.toString().isEmpty() ?
                        "IOException occurred while reading a line from the buffered reader. No characters were parsed," :
                        String.format("IOException occurred while reading a line from the buffered reader. " +
                                "Could only read the input stream up to this point: \n%s", stringBuilder);
                throw new StreamParsingException(message, e);
            }

            if (!contentLengthFound && line.startsWith(contentLengthHeaderKey)) {
                contentLengthFound = true;
                contentLength = Integer.parseInt(line.substring(contentLengthHeaderKey.length() + 1).trim());
            }

            if (line.isEmpty()) {
                stringBuilder.append(System.lineSeparator());
                if (contentLengthFound) {
                    for (int i = 0; i < contentLength; i++) {
                        try {
                            stringBuilder.append((char) bufferedReader.read());
                        } catch (IOException e) {
                            val message = String.format("IOException occurred while reading a character from the buffered reader. " +
                                    "Could only read the input stream up to this point: \n%s", stringBuilder);
                            throw new StreamParsingException(message, e);
                        }
                    }
                }
                break;
            }
            stringBuilder.append(line).append(System.lineSeparator());
        }

        String result = stringBuilder.toString();
        log.atFine().log("Parsed the following string from the input stream: %s", result);
        return result;
    }
}
