package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto create(Long userId, CreateItemDto createItemDto);

    ItemDto update(Long userId, Long itemId, ItemDto itemDto);

    void delete(Long userId, Long itemId);

    ItemDto findById(Long id);

    List<ItemDto> findByUser(Long userId);

    List<ItemDto> findByText(String text);
}
