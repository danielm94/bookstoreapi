package com.github.danielm94.server.executors;

import java.util.concurrent.Executor;

public class BookServerExecutor implements Executor {
    @Override
    public void execute(Runnable command) {
        command.run();
    }
}