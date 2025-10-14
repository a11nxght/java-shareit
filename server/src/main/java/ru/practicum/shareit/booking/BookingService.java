package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;

import java.util.List;

public interface BookingService {
    BookingDto create(NewBookingRequest newBookingRequest, long userId);

    BookingDto approveOrReject(Long ownerId, Long bookingId, boolean approved);

    BookingDto getById(Long userId, Long bookingId);

    List<BookingDto> getALLByBookerId(Long bookerId, String state);

    List<BookingDto> getAllByItemOwnerId(Long ownerId, String state);
}
