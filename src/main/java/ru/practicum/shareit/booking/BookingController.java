package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                             @Valid @RequestBody NewBookingRequest newBookingRequest) {
        return bookingService.create(newBookingRequest, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveOrReject(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                      @PathVariable Long bookingId,
                                      @RequestParam("approved") boolean approved) {
        return bookingService.approveOrReject(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable long bookingId) {
        return bookingService.getById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getAllByBookerId(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                             @RequestParam(name = "state", defaultValue = "All") String state) {
        return bookingService.getALLByBookerId(bookerId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllByItemOwnerId(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                @RequestParam(name = "state", defaultValue = "All") String state) {
        return bookingService.getAllByItemOwnerId(ownerId, state);
    }
}
