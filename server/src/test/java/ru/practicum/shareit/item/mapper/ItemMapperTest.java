package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingSmallDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class ItemMapperTest {
    @Test
    void toItemDto_mapsAllFields() {
        User owner = new User();
        owner.setId(1L);
        ItemRequest request = new ItemRequest();
        request.setId(99L);

        Item item = new Item();
        item.setId(10L);
        item.setName("Drill");
        item.setDescription("Cordless");
        item.setAvailable(true);
        item.setOwner(owner);
        item.setRequest(request);

        ItemDto dto = ItemMapper.toItemDto(item);

        assertThat(dto.getId()).isEqualTo(10L);
        assertThat(dto.getName()).isEqualTo("Drill");
        assertThat(dto.getDescription()).isEqualTo("Cordless");
        assertThat(dto.getAvailable()).isTrue();
        assertThat(dto.getRequestId()).isEqualTo(99L);
    }

    @Test
    void toItemDto_nullRequest_setsNullRequestId() {
        Item item = new Item();
        item.setId(5L);
        item.setName("Lamp");
        item.setDescription("Desk lamp");
        item.setAvailable(false);

        ItemDto dto = ItemMapper.toItemDto(item);
        assertThat(dto.getRequestId()).isNull();
    }

    @Test
    void toItem_mapsFieldsToEntity() {
        User owner = new User();
        owner.setId(7L);

        ItemDto dto = new ItemDto();
        dto.setName("Saw");
        dto.setDescription("Sharp");
        dto.setAvailable(true);

        Item entity = ItemMapper.toItem(dto, owner);

        assertThat(entity.getName()).isEqualTo("Saw");
        assertThat(entity.getDescription()).isEqualTo("Sharp");
        assertThat(entity.isAvailable()).isTrue();
        assertThat(entity.getOwner()).isSameAs(owner);
    }

    @Test
    void toItemWithBookingDto_mapsAllIncludingBookings() {
        Item item = new Item();
        item.setId(22L);
        item.setName("Bike");
        item.setDescription("Fast");
        item.setAvailable(true);

        BookingSmallDto last = new BookingSmallDto();
        last.setId(1L);
        BookingSmallDto next = new BookingSmallDto();
        next.setId(2L);

        ItemWithBookingDto dto = ItemMapper.toItemWithBookingDto(item, last, next);

        assertThat(dto.getId()).isEqualTo(22L);
        assertThat(dto.getName()).isEqualTo("Bike");
        assertThat(dto.getLastBooking().getId()).isEqualTo(1L);
        assertThat(dto.getNextBooking().getId()).isEqualTo(2L);
    }
}