package com.github.danielm94.server.parsers.requestline;

import com.github.danielm94.server.HttpMethod;
import lombok.extern.flogger.Flogger;
import lombok.val;
import org.apache.commons.lang3.StringUtils;

@Flogger
public class DefaultRequestLineParserStrategy implements RequestLineParserStrategy {
    public static final String SPLIT_USING_WHITESPACE_REGEX_PATTERN = "\\s";
    public static final int NUMBER_OF_COMPONENTS_IN_REQUEST_LINE = 3;

    @Override
    public RequestLine parseRequestLine(String requestLineString) throws RequestLineParsingException {
        if (StringUtils.isBlank(requestLineString)) {
            throw new RequestLineParsingException("Could not parse request line as it was null/empty/blank.", null);
        }
        val requestLine = new RequestLine();

        val requestLineValues = requestLineString.split(SPLIT_USING_WHITESPACE_REGEX_PATTERN);

        if (requestLineValues.length < NUMBER_OF_COMPONENTS_IN_REQUEST_LINE) {
            val message = String.format("The request line sent by the client did not include all %s expected components. " +
                    "Request line: %s", NUMBER_OF_COMPONENTS_IN_REQUEST_LINE, requestLineString);
            throw new RequestLineParsingException(message);
        }

        val requestMethodString = requestLineValues[0];
        HttpMethod requestMethod = null;
        try {
            requestMethod = HttpMethod.valueOf(requestMethodString);
        } catch (IllegalArgumentException e) {
            log.atWarning()
               .log("Client sent an unsupported request method - [%s]", requestMethodString);
        }
        requestLine.setHttpMethod(requestMethod);

        val path = requestLineValues[1];
        requestLine.setPath(path);

        val protocol = requestLineValues[2];
        requestLine.setProtocol(protocol);

        return requestLine;
    }

}
