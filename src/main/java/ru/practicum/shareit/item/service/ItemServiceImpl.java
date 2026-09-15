package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeptions.ItemOwnershipException;
import ru.practicum.shareit.exeptions.NotFoundException;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;


import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto create(Long userId, CreateItemDto createItemDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Item item = ItemMapper.toItem(createItemDto);
        item.setOwner(user);
        Item savedItem = itemRepository.save(item);
        log.info("Item created: {}", savedItem);
        return ItemMapper.toItemDto(savedItem);
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Item item =  itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Item not found"));
        if (isOwner(item,user)) {
            Item reqItem = ItemMapper.toItem(itemDto);
            reqItem.setOwner(user);
            reqItem.setId(itemId);
            Item updatedItem = itemRepository.update(itemId, reqItem);
            log.info("Item updated: {}", updatedItem);
            return ItemMapper.toItemDto(updatedItem);
        } else {
            throw new ItemOwnershipException("User is not owner of this item");
        }

    }

    @Override
    public void delete(Long userId, Long itemId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));
        if (isOwner(item, user)) {
            itemRepository.delete(itemId);
            log.info("Item deleted: {}", itemId);
        } else {
            throw new ItemOwnershipException("User is not owner of this item");
        }
    }

    @Override
    public ItemDto findById(Long id) {
        return itemRepository.findById(id)
                .map(ItemMapper::toItemDto)
                .orElseThrow(() -> new NotFoundException("Item not found"));
    }

    @Override
    public List<ItemDto> findByUser(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return itemRepository.findByUser(userId).stream()
                .map(ItemMapper::toItemDto).toList();
    }

    @Override
    public List<ItemDto> findByText(String text) {
        return itemRepository.findByText(text).stream().map(ItemMapper::toItemDto).toList();
    }

    private boolean isOwner(Item item, User user) {
        return item.getOwner().getId().equals(user.getId());
    }
}
