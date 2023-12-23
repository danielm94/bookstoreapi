package com.github.danielm94.server;

import lombok.val;

import java.util.HashMap;
import java.util.Map;

public enum HttpMethod {
    GET,
    HEAD,
    POST,
    PUT,
    DELETE,
    CONNECT,
    OPTIONS,
    TRACE,
    PATCH;

    private static final Map<String, HttpMethod> nameMethodMap;

    static {
        nameMethodMap = new HashMap<>();
        for (val value : HttpMethod.values()) {
            nameMethodMap.put(value.toString().toUpperCase(), value);
        }
    }


    public static HttpMethod getHttpMethodFromStringValue(String httpMethodString) {
        return nameMethodMap.get(httpMethodString.toUpperCase());
    }
}
