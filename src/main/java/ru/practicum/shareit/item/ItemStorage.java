package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    Item add(Item item);

    Item get(long id);

    Item update(Item item);

    List<Item> getItems(long userId);

    List<Item> searchItems(String text, long userId);
}
