package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;

@Slf4j
@Component
public class ItemRepository implements ItemStorage {
    private long itemId = 0;
    private final HashMap<Long, Item> items = new HashMap<>();

    @Override
    public Item add(Item item) {
        item.setId(++itemId);
        items.put(itemId, item);
        return item;
    }

    @Override
    public Item get(long id) {
        if (items.containsKey(id)) {
            return items.get(id);
        }
        log.warn("При запросе данных возникла ошибка: вещь c id({}) не найдена", id);
        throw new NotFoundException("Вещь " + id + " не найдена");
    }

    @Override
    public Item update(Item item) {
        if (items.containsKey(item.getId())) {
            items.replace(item.getId(), item);
            return item;
        }
        log.warn("При обновлении данных возникла ошибка: вещь c id({}) не найдена", item.getId());
        throw new NotFoundException("Вещь " + item.getId() + " не найдена");
    }

    @Override
    public List<Item> getItems(long userId) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId() == userId)
                .toList();
    }

    @Override
    public List<Item> searchItems(String text, long userId) {
        return items.values()
                .stream()
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase()) ||
                        item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .filter(Item::isAvailable).toList();

    }
}
