package com.github.danielm94.server.handlers;


import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;

@SuppressWarnings("DataFlowIssue")
class ExceptionHandlerTest {

    @Test
    void nullPointerExceptionShouldBeThrownWhenPassingNullMessage() {
        assertThatThrownBy(() -> new ExceptionHandler(null, any(Throwable.class)))
                .as("NullPointerException should be thrown if a null log message is passed.")
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void nullPointerExceptionShouldBeThrownWhenPassingNullThrowable() {
        assertThatThrownBy(() -> new ExceptionHandler("Whatever", null))
                .as("NullPointerException should be thrown if a null throwable is passed.")
                .isInstanceOf(NullPointerException.class);
    }
}