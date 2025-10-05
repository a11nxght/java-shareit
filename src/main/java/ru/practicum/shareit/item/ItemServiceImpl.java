package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserStorage;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService{

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public ItemDto create(ItemDto itemDto, long userId) {
        User user = userStorage.get(userId);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(user);
        item = itemStorage.add(item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto update(ItemDto itemDto, long itemId, long userId) {
        Item item = itemStorage.get(itemId);
        if (!item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("У данного пользователя нет вещи с таким id.");
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        item = itemStorage.update(item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto getItem(long id) {
        return ItemMapper.toItemDto(itemStorage.get(id));
    }

    @Override
    public List<ItemDto> getItems(long userId) {
        return itemStorage.getItems(userId).stream().map(ItemMapper::toItemDto).toList();
    }

    @Override
    public List<ItemDto> searchItems(String text, long userId) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return itemStorage.searchItems(text, userId).stream().map(ItemMapper::toItemDto).toList();
    }
}
