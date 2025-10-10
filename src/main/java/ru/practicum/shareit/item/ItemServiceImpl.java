package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ItemDto create(ItemDto itemDto, long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.warn("Unable to create item. User not found.");
            return new NotFoundException("User not found.");
        });
        Item item = itemRepository.save(ItemMapper.toItem(itemDto, user));
        return ItemMapper.toItemDto(item);
    }

    @Override
    @Transactional
    public ItemDto update(ItemDto itemDto, long itemId, long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> {
            log.warn("Unable to update item. Item not found.");
            return new NotFoundException("Item not found.");
        });
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
        item = itemRepository.save(item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto getItem(long id) {
        return ItemMapper.toItemDto(itemRepository.findById(id).orElseThrow(() -> {
            log.warn("Unable to get item. Item not found.");
            return new NotFoundException("Item not found.");
        }));
    }

    @Override
    public List<ItemDto> getItems(long userId) {
        return itemRepository.findAllByOwnerId(userId).stream().map(ItemMapper::toItemDto).toList();
    }

    @Override
    public List<ItemDto> searchItems(String text, long userId) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return itemRepository.search(text).stream().map(ItemMapper::toItemDto).toList();
    }
}
