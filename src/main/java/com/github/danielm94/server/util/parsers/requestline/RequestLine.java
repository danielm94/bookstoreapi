package com.github.danielm94.server.util.parsers.requestline;

import com.github.danielm94.server.HttpMethod;
import lombok.Data;


@Data
public class RequestLine {
    private HttpMethod httpMethod;
    private String path;
    private String protocol;
}
