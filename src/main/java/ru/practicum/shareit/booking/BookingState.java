package ru.practicum.shareit.booking;

import ru.practicum.shareit.exceptions.BadRequestException;

public enum BookingState {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static BookingState from(String value) {
        try {
            return value == null ? ALL : BookingState.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Unknown state: " + value);
        }
    }
}
