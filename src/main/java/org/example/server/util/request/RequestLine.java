package org.example.server.util.request;

import lombok.Data;
import org.example.server.HttpMethod;


@Data
public class RequestLine {
    private HttpMethod httpMethod;
    private String path;
    private String protocol;
}
