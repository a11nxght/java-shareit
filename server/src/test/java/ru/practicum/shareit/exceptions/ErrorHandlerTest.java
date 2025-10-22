package ru.practicum.shareit.exceptions;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class ErrorHandlerTest {

    private final ErrorHandler handler = new ErrorHandler();

    @Test
    void notFoundHandle_returnsErrorResponseWithMessage() {
        ErrorResponse res = handler.notFoundHandle(new NotFoundException("not-found msg"));
        assertThat(res.getError()).isEqualTo("not-found msg");
    }

    @Test
    void duplicatedDataHandle_returnsErrorResponseWithMessage() {
        ErrorResponse res = handler.duplicatedDataHandle(new DuplicatedDataException("conflict msg"));
        assertThat(res.getError()).isEqualTo("conflict msg");
    }

    @Test
    void badRequestHandle_returnsErrorResponseWithMessage() {
        ErrorResponse res = handler.badRequestHandle(new BadRequestException("bad msg"));
        assertThat(res.getError()).isEqualTo("bad msg");
    }

    @Test
    void forbiddenHandle_returnsErrorResponseWithMessage() {
        ErrorResponse res = handler.forbiddenHandle(new ForbiddenException("forbidden msg"));
        assertThat(res.getError()).isEqualTo("forbidden msg");
    }
}
