package com.github.danielm94.server;

import com.github.danielm94.server.context.BookContext;
import com.github.danielm94.server.parsers.body.DefaultBodyParserStrategy;
import com.github.danielm94.server.parsers.headers.DefaultHeaderParserStrategy;
import com.github.danielm94.server.parsers.requestline.DefaultRequestLineParserStrategy;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.flogger.Flogger;
import lombok.val;
import com.github.danielm94.server.processors.RequestProcessor;
import com.github.danielm94.server.parsers.RequestDataParser;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

@Flogger
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

        while (serverIsRunning) {
            val clientSocket = socket.accept();
            log.atFine().log("Accepted new client socket...");
            val parser = new RequestDataParser(contextMap,
                    new DefaultRequestLineParserStrategy(),
                    new DefaultHeaderParserStrategy(),
                    new DefaultBodyParserStrategy());
            var exchange = parser.getBookHttpExchangeFromClientSocket(clientSocket);
            val requestProcessor = new RequestProcessor(clientSocket, exchange);
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
        var bookContext = new BookContext();
        bookContext.setServer(this);
        bookContext.setPath(path);
        bookContext.setHandler(handler);
        contextMap.put(path, bookContext);
        return bookContext;
    }

    @Override
    public HttpContext createContext(String path) {
        var bookContext = new BookContext();
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
}
