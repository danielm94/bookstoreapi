package com.github.danielm94.server.executors;

import lombok.NonNull;

import java.util.concurrent.Executor;

public class BookServerExecutor implements Executor {
    @Override
    public void execute(@NonNull Runnable command) {
        command.run();
    }
}
