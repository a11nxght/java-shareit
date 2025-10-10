package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService{

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public BookingDto create(NewBookingRequest newBookingRequest, long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.warn("Unable to create booking. User not found.");
            return new NotFoundException("User not found.");
        });
        Item item = itemRepository.findById(newBookingRequest.getItemId()).orElseThrow(() -> {
            log.warn("Unable to create booking. Item not found.");
            return new NotFoundException("Item not found.");});
        Booking booking = bookingRepository.save(BookingMapper.toBooking(newBookingRequest, item, user));
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    @Transactional
    public BookingDto approveOrReject(Long ownerId, Long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            log.warn("Unable to approve or reject booking. Booking not found.");
            return new NotFoundException("Booking not found.");
        });
        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            log.warn("Unable to approve or reject booking. Only the owner of the item can approve or reject the booking.");
            throw new NotFoundException("Unable to approve or reject booking. " +
                    "This user does not have an item with this id.");
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        booking = bookingRepository.save(booking);
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto getById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            log.warn("Unable to get booking. Booking not found.");
            return new NotFoundException("Booking not found.");
        });
        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            log.warn("Unable to get booking. Only the owner or the booker of the item can get the booking.");
            throw new NotFoundException("Unable to get booking. " +
                    "Only the owner or the booker of the item can get the booking.");
        }
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getALLByBookerId(Long bookerId, String stateStr) {
        BookingState state = BookingState.from(stateStr);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = new ArrayList<>();
        switch (state) {
            case ALL -> bookings = bookingRepository
                    .findAllByBookerIdOrderByStartDesc(bookerId);
            case CURRENT -> bookings = bookingRepository
                    .findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(bookerId, now, now);
            case PAST -> bookings = bookingRepository
                    .findByBookerIdAndEndBeforeOrderByStartDesc(bookerId, now);
            case FUTURE -> bookings = bookingRepository
                    .findByBookerIdAndStartAfterOrderByStartDesc(bookerId, now);
            case WAITING -> bookings = bookingRepository
                    .findByBookerIdAndStatusOrderByStartDesc(bookerId, BookingStatus.WAITING);
            case REJECTED -> bookings = bookingRepository
                    .findByBookerIdAndStatusOrderByStartDesc(bookerId, BookingStatus.REJECTED);
        }
        return bookings.stream().map(BookingMapper::toBookingDto).toList();
    }

    @Override
    public List<BookingDto> getAllByItemOwnerId(Long ownerId, String stateStr) {
        userRepository.findById(ownerId).orElseThrow(() -> {
            log.warn("Unable to get bookings. User not found.");
            return new NotFoundException("User not found.");
        });
        BookingState state = BookingState.from(stateStr);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = new ArrayList<>();
        switch (state) {
            case ALL -> bookings = bookingRepository
                    .findAllByItemOwnerIdOrderByStartDesc(ownerId);
            case CURRENT -> bookings = bookingRepository
                    .findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(ownerId, now, now);
            case PAST -> bookings = bookingRepository
                    .findByItemOwnerIdAndEndBeforeOrderByStartDesc(ownerId, now);
            case FUTURE -> bookings = bookingRepository
                    .findByItemOwnerIdAndStartAfterOrderByStartDesc(ownerId, now);
            case WAITING -> bookings = bookingRepository
                    .findByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.WAITING);
            case REJECTED -> bookings = bookingRepository
                    .findByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.REJECTED);
        }
        return bookings.stream().map(BookingMapper::toBookingDto).toList();
    }
}
