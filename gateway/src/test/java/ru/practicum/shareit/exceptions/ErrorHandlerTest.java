package ru.practicum.shareit.exceptions;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class ErrorHandlerTest {

    private final ErrorHandler handler = new ErrorHandler();

    @Test
    void badRequestHandle_returnsErrorResponseWithMessage() {
        ErrorResponse res = handler.badRequestHandle(new BadRequestException("bad msg"));
        assertThat(res.getError()).isEqualTo("bad msg");
    }

    @Test
    void handleThrowable_returnsErrorResponseWithMessage() {
        ErrorResponse res = handler.handleThrowable(new Throwable("throwable msg"));
        assertThat(res.getError()).isEqualTo("throwable msg");
    }
}
