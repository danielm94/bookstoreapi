package org.example.server.socket;

import com.sun.net.httpserver.HttpContext;
import lombok.SneakyThrows;
import lombok.val;
import org.example.server.exchange.BookHttpExchange;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.Scanner;


public class ClientSocketParser {
    private final Map<String, HttpContext> contextMap;

    public ClientSocketParser(Map<String, HttpContext> contextMap) {
        this.contextMap = contextMap;
    }

    @SneakyThrows(IOException.class)
    public static BookHttpExchange getBookHttpExchangeFromClientSocket(Socket clientSocket) {
        val inputStream = clientSocket.getInputStream();
        val scanner = new Scanner(inputStream);

        return null;
    }
//    clientSocketText = {
//        ArrayList @923}  size = 12
//            0 = "GET / HTTP/1.1"
//            1 = "Content-Type: application/json"
//            2 = "User-Agent: PostmanRuntime/7.36.0"
//            3 = "Accept: */*"
//            4 = "Cache-Control: no-cache"
//            5 = "Postman-Token: c60dfa73-b015-46d4-b9be-89099876e138"
//            6 = "Host: localhost:8000"
//            7 = "Accept-Encoding: gzip, deflate, br"
//            8 = "Connection: keep-alive"
//            9 = "Content-Length: 19"
//            10 = ""
//            11 = "Hello from Postman!"
}
