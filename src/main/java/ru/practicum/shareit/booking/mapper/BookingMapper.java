package ru.practicum.shareit.booking.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingSmallDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {
    public static BookingDto toBookingDto(Booking booking) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setStatus(booking.getStatus());
        bookingDto.setBooker(UserMapper.mapToUserDto(booking.getBooker()));
        bookingDto.setItem(ItemMapper.toItemDto(booking.getItem()));
        return bookingDto;
    }

    public static Booking toBooking(NewBookingRequest newBookingRequest, Item item, User user) {
        Booking booking = new Booking();
        booking.setStart(newBookingRequest.getStart());
        booking.setEnd(newBookingRequest.getEnd());
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.WAITING);
        return booking;
    }

    public static BookingSmallDto toBookingSmallDto(Booking booking) {
        BookingSmallDto bookingSmallDto = new BookingSmallDto();
        bookingSmallDto.setId(booking.getId());
        bookingSmallDto.setStart(booking.getStart());
        bookingSmallDto.setEnd(booking.getEnd());
        bookingSmallDto.setBookerId(booking.getBooker().getId());
        return bookingSmallDto;
    }
}
