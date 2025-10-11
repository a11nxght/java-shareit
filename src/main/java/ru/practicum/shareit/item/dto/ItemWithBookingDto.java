package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingSmallDto;

import java.util.List;

@Data
@NoArgsConstructor
public class ItemWithBookingDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingSmallDto lastBooking;
    private BookingSmallDto nextBooking;
    private List<CommentDto> comments;
}
