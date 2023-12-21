package org.example;

import org.example.server.BookServer;
import org.example.server.executors.BookServerExecutor;
import org.example.server.handlers.RequestHandler;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public class Main {
    public static void main(String[] args) {
        var address = new InetSocketAddress(InetAddress.getLoopbackAddress(), 8000);
        var server = new BookServer();
        server.bind(address, 0);
        server.setExecutor(new BookServerExecutor());

        var context = server.createContext("/");
        context.setHandler(new RequestHandler());
        server.start();

    }
}