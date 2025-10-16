package ru.practicum.shareit.booking.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import ru.practicum.shareit.exceptions.BadRequestException;

import static org.junit.jupiter.api.Assertions.*;

class BookingStateTest {

    @Test
    void from_null_returnsALL() {
        assertEquals(BookingState.ALL, BookingState.from(null));
    }

    @ParameterizedTest
    @CsvSource({
            "all,ALL",
            "All,ALL",
            "ALL,ALL",
            "current,CURRENT",
            "Current,CURRENT",
            "past,PAST",
            "future,FUTURE",
            "waiting,WAITING",
            "rejected,REJECTED"
    })
    void from_caseInsensitive_valid(String input, BookingState expected) {
        assertEquals(expected, BookingState.from(input));
    }

    @Test
    void from_unknown_throwsBadRequest() {
        BadRequestException ex = assertThrows(BadRequestException.class, () -> BookingState.from("foo"));
        assertEquals("Unknown state: foo", ex.getMessage());
    }
}
