package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;

import java.util.List;

public interface ItemService {
    ItemDto create(ItemDto itemDto, long userId);

    ItemDto update(ItemDto itemDto, long itemId, long userId);

    ItemDto getItem(long id);

    List<ItemWithBookingDto> getItems(long userId);

    List<ItemDto> searchItems(String text, long userId);
}
