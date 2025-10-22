package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.ForbiddenException;
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
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public BookingDto create(NewBookingRequest newBookingRequest, long userId) {
        if (newBookingRequest.getStart() == null || newBookingRequest.getEnd() == null) {
            throw new BadRequestException("Start and end must be provided.");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.warn("Unable to create booking. User not found.");
            return new NotFoundException("User not found.");
        });
        Item item = itemRepository.findById(newBookingRequest.getItemId()).orElseThrow(() -> {
            log.warn("Unable to create booking. Item not found.");
            return new NotFoundException("Item not found.");
        });
        if (!item.isAvailable()) {
            log.warn("Unable to create booking. Item is not available for booking.");
            throw new BadRequestException("Item is not available for booking.");
        }
        if (item.getOwner().getId().equals(userId)) {
            log.warn("Unable to create booking. Owner can not book.");
            throw new BadRequestException("Owner can not book.");
        }

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
            throw new ForbiddenException("Unable to approve or reject booking. " +
                    "This user does not have an item with this id.");
        }
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new BadRequestException("Booking status is already decided.");
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
    public List<BookingDto> getALLByBookerId(Long bookerId, BookingState state) {
        LocalDateTime now = LocalDateTime.now();
        Sort newestFirst = Sort.by(Sort.Direction.DESC, "start");
        List<Booking> bookings = new ArrayList<>();
        switch (state) {
            case ALL -> bookings = bookingRepository
                    .findAllByBookerId(bookerId, newestFirst);
            case CURRENT -> bookings = bookingRepository
                    .findByBookerIdAndStartBeforeAndEndAfter(bookerId, now, now, newestFirst);
            case PAST -> bookings = bookingRepository
                    .findByBookerIdAndEndBefore(bookerId, now, newestFirst);
            case FUTURE -> bookings = bookingRepository
                    .findByBookerIdAndStartAfter(bookerId, now, newestFirst);
            case WAITING -> bookings = bookingRepository
                    .findByBookerIdAndStatus(bookerId, BookingStatus.WAITING, newestFirst);
            case REJECTED -> bookings = bookingRepository
                    .findByBookerIdAndStatus(bookerId, BookingStatus.REJECTED, newestFirst);
        }
        return bookings.stream().map(BookingMapper::toBookingDto).toList();
    }

    @Override
    public List<BookingDto> getAllByItemOwnerId(Long ownerId, BookingState state) {
        userRepository.findById(ownerId).orElseThrow(() -> {
            log.warn("Unable to get bookings. User not found.");
            return new NotFoundException("User not found.");
        });
        LocalDateTime now = LocalDateTime.now();
        Sort newestFirst = Sort.by(Sort.Direction.DESC, "start");
        List<Booking> bookings = new ArrayList<>();
        switch (state) {
            case ALL -> bookings = bookingRepository
                    .findAllByItemOwnerId(ownerId, newestFirst);
            case CURRENT -> bookings = bookingRepository
                    .findByItemOwnerIdAndStartBeforeAndEndAfter(ownerId, now, now, newestFirst);
            case PAST -> bookings = bookingRepository
                    .findByItemOwnerIdAndEndBefore(ownerId, now, newestFirst);
            case FUTURE -> bookings = bookingRepository
                    .findByItemOwnerIdAndStartAfter(ownerId, now, newestFirst);
            case WAITING -> bookings = bookingRepository
                    .findByItemOwnerIdAndStatus(ownerId, BookingStatus.WAITING, newestFirst);
            case REJECTED -> bookings = bookingRepository
                    .findByItemOwnerIdAndStatus(ownerId, BookingStatus.REJECTED, newestFirst);
        }
        return bookings.stream().map(BookingMapper::toBookingDto).toList();
    }
}
