package ru.practicum.shareit.item.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingSmallDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.isAvailable());
        itemDto.setRequestId(item.getRequest() != null ? item.getRequest().getId() : null);
        return itemDto;
    }

    public static Item toItem(ItemDto itemDto, User user) {
        Item item = new Item();
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwner(user);
        return item;
    }

    public static ItemWithBookingDto toItemWithBookingDto(Item item, BookingSmallDto lastBooking,
                                                          BookingSmallDto nextBooking) {
        ItemWithBookingDto itemWithBookingDto = new ItemWithBookingDto();
        itemWithBookingDto.setId(item.getId());
        itemWithBookingDto.setName(item.getName());
        itemWithBookingDto.setDescription(item.getDescription());
        itemWithBookingDto.setAvailable(item.isAvailable());
        itemWithBookingDto.setLastBooking(lastBooking);
        itemWithBookingDto.setNextBooking(nextBooking);
        return itemWithBookingDto;
    }
}
