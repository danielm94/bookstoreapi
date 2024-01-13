package com.github.danielm94.server;

import com.github.danielm94.server.context.BookContext;
import com.github.danielm94.server.processors.RequestProcessor;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.flogger.Flogger;
import lombok.val;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.regex.Pattern;

@Flogger
public class BookServer extends HttpServer {
    private final Map<Pattern, Function<Map<String, String>, HttpContext>> dynamicPaths;
    private final Map<String, HttpContext> contextMap;
    private boolean serverIsRunning;
    private ServerSocket socket;
    private InetSocketAddress address;
    private int backlog;
    private Executor executor;


    public BookServer() {
        this.contextMap = new HashMap<>();
        this.dynamicPaths = new HashMap<>();
    }

    @Override
    public void bind(@NonNull InetSocketAddress addr, int backlog) {
        address = addr;
        this.backlog = backlog;
    }


    @Override
    public void start() {
        if (!instantiateServer()) return;
        serverIsRunning = true;

        while (serverIsRunning) {
            Socket clientSocket;

            try {
                clientSocket = socket.accept();
                log.atFine().log("Accepted new client socket...");
            } catch (IOException e) {
                log.atWarning().withCause(e).log("Exception occurred while accepting a connection");
                return;
            }

            val requestProcessor = new RequestProcessor(clientSocket, contextMap, dynamicPaths);
            executor.execute(requestProcessor);
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
        val bookContext = new BookContext();
        bookContext.setServer(this);
        bookContext.setPath(path);
        bookContext.setHandler(handler);
        contextMap.put(path, bookContext);
        return bookContext;
    }

    @Override
    public HttpContext createContext(String path) {
        val bookContext = new BookContext();
        bookContext.setServer(this);
        bookContext.setPath(path);
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

    public void registerDynamicPath(@NonNull Pattern pattern, @NonNull Function<Map<String, String>, HttpContext> routeFunction) {
        dynamicPaths.put(pattern, routeFunction);
    }

    private boolean instantiateServer() {
        try {
            socket = new ServerSocket(address.getPort(), backlog, address.getAddress());
        } catch (IOException e) {
            log.atSevere().withCause(e).log("Failed to bind to address: " + address);
            return false;
        }
        return true;
    }
}
