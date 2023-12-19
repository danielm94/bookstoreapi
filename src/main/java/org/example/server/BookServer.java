package org.example.server;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.example.server.context.BookContext;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.Executor;

public class BookServer extends HttpServer {
    private final Map<String, HttpContext> contextMap;
    private boolean serverIsRunning;
    private ServerSocket socket;
    private InetSocketAddress address;
    private int backlog;
    private Executor executor;


    public BookServer() {
        this.contextMap = new HashMap<>();
    }

    @Override
    public void bind(@NonNull InetSocketAddress addr, int backlog) {
        this.address = addr;
        this.backlog = backlog;
    }



    @Override
    @SneakyThrows(IOException.class)
    public void start() {
        this.socket = new ServerSocket(address.getPort(), backlog, address.getAddress());
        serverIsRunning = true;
        var clientSocket = socket.accept();
        var clientSocketText = new ArrayList<String>();
        var scanner = new Scanner(clientSocket.getInputStream());
        while(scanner.hasNext()){
            clientSocketText.add(scanner.nextLine());
        }
    }

    @Override
    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    @Override
    public Executor getExecutor() {
        return executor;
    }

    @Override
    @SneakyThrows(IOException.class)
    public void stop(int delay) {
        serverIsRunning = false;
        if (socket != null) socket.close();
    }

    @Override
    public HttpContext createContext(String path, HttpHandler handler) {
        var bookContext = new BookContext(this, path);
        bookContext.setHandler(handler);
        contextMap.put(path, bookContext);
        return bookContext;
    }

    @Override
    public HttpContext createContext(String path) {
        var bookContext = new BookContext(this, path);
        contextMap.put(path, bookContext);
        return bookContext;
    }

    @Override
    public void removeContext(String path) throws IllegalArgumentException {
        contextMap.remove(path);
    }

    @Override
    public void removeContext(HttpContext context) {
        for (var key : contextMap.keySet()) {
            if (contextMap.get(key).equals(context)) contextMap.remove(key);
        }
    }

    @Override
    public InetSocketAddress getAddress() {
        return address;
    }
}
