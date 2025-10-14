package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.model.BookingState;


@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @Valid @RequestBody NewBookingRequest newBookingRequest) {
        return bookingClient.create(userId, newBookingRequest);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveOrReject(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                  @PathVariable Long bookingId,
                                                  @RequestParam("approved") boolean approved) {
        return bookingClient.approveOrReject(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @PathVariable long bookingId) {
        return bookingClient.getById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByBookerId(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                                   @RequestParam(name = "state", defaultValue = "All") String state) {
        BookingState bookingState = BookingState.from(state);
        return bookingClient.getAllByBookerId(bookerId, bookingState);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllByItemOwnerId(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                      @RequestParam(name = "state", defaultValue = "All") String state) {
        BookingState bookingState = BookingState.from(state);
        return bookingClient.getAllByItemOwnerId(ownerId, bookingState);
    }
}
