package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;

import java.util.List;

public interface ItemService {
    ItemDto create(ItemDto itemDto, long userId);

    ItemDto update(ItemDto itemDto, long itemId, long userId);

    ItemWithBookingDto getItem(long id, long userId);

    List<ItemWithBookingDto> getItems(long userId);

    List<ItemDto> searchItems(String text, long userId);

    CommentDto createComment(CommentDto commentDto, long authorId, long itemId);
}
