package com.github.danielm94.server.executors;

import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

class BookServerExecutorTest {
    private BookServerExecutor executor;

    @BeforeEach
    void setUp() {
        this.executor = new BookServerExecutor();
    }

    @Test
    void exceptionIsThrownWhenPassingANullRunnable() {
        assertThatThrownBy(() -> executor.execute(null))
                .as("NullPointerException should be thrown when passing null runnable as a parameter.")
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void executeRunnableIsRun() {
        val runnable = mock(Runnable.class);

        executor.execute(runnable);
        verify(runnable, times(1)).run();
    }
}