package com.github.danielm94.server.handlers.book.httpmethod.factory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SuppressWarnings("DataFlowIssue")
class DefaultHttpMethodBookHandlerFactoryTest {
    private DefaultHttpMethodBookHandlerFactory factory;

    @BeforeEach
    void setUp() {
        factory = new DefaultHttpMethodBookHandlerFactory();
    }

    @Test
    void nullPointerExceptionShouldBeThrownWhenPassingNullHttpMethodToGetHandler() {
        assertThatThrownBy(() -> factory.getHandler(null))
                .as("NullPointerException should be thrown if a null HttpMethod is passed.")
                .isInstanceOf(NullPointerException.class);
    }

}