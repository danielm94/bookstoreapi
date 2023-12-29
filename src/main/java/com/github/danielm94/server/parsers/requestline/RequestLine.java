package com.github.danielm94.server.parsers.requestline;

import com.github.danielm94.server.requestdata.method.HttpMethod;
import lombok.Data;


@Data
public class RequestLine {
    private HttpMethod httpMethod;
    private String path;
    private String protocol;
}
