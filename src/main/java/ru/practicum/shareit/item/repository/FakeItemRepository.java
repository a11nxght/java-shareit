package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exeptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Repository
public class FakeItemRepository implements ItemRepository {

    private final Map<Long, Item> items = new HashMap<>();
    private long lastId = 0;

    @Override
    public Item save(Item item) {
        item.setId(++lastId);
        items.put(item.getId(), item);
        return items.get(item.getId());
    }

    @Override
    public Item update(Long itemId, Item item) {
        Item itemToUpdate = items.get(itemId);
        if (itemToUpdate == null) {
            throw new NotFoundException("Item with id " + itemId + " not found");
        }
        if (item.getName() != null) {
            itemToUpdate.setName(item.getName());
        }
        if (item.getDescription() != null) {
            itemToUpdate.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            itemToUpdate.setAvailable(item.getAvailable());
        }
        return items.get(itemId);
    }

    @Override
    public void delete(Long id) {
        items.remove(id);
    }

    @Override
    public Optional<Item> findById(Long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public List<Item> findByUser(Long userId) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId() == userId.longValue())
                .toList();
    }

    @Override
    public List<Item> findAll() {
        return items.values().stream().toList();
    }

    @Override
    public List<Item> findByText(String text) {
        if (text.isBlank()) {
            return List.of();
        }
        return items.values().stream()
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .filter(item -> item.getAvailable() == true)
                .toList();
    }
}
