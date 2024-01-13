package com.github.danielm94.server.domain.book.serializer.factory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SuppressWarnings("DataFlowIssue")
class DefaultBookSerializerFactoryTest {
    private DefaultBookSerializerFactory factory;

    @BeforeEach
    void setUp() {
        factory = new DefaultBookSerializerFactory();
    }

    @Test
    void nullPointerExceptionShouldBeThrownWhenPassingNullContentTypeToGetSerializer() {
        assertThatThrownBy(() -> factory.getSerializer(null))
                .as("NullPointerException should be thrown if a null ContentType is passed.")
                .isInstanceOf(NullPointerException.class);
    }
}