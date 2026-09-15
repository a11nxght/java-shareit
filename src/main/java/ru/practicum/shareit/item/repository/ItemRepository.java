package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    Item save(Item item);

    Item update(Long itemId, Item item);

    void delete(Long id);

    Optional<Item> findById(Long id);

    List<Item> findByUser(Long userId);

    List<Item> findAll();

    List<Item> findByText(String text);
}
