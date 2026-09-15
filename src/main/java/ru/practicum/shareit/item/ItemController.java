package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @Valid @RequestBody CreateItemDto createItemDto) {
        log.info("Trying to create item {}", createItemDto);
        return itemService.create(userId, createItemDto);
    }

    @PatchMapping("{itemId}")
    public ItemDto updateItem(@PathVariable Long itemId,
                              @RequestHeader("X-Sharer-User-Id") Long userId,
                              @Valid @RequestBody ItemDto itemDto) {
        log.info("Trying to update item {}", itemDto);
        return itemService.update(userId, itemId, itemDto);
    }

    @DeleteMapping("{itemId}")
    public void deleteItem(@PathVariable Long itemId,
                           @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Trying to delete item with id: {}", itemId);
        itemService.delete(userId, itemId);
    }

    @GetMapping("{itemId}")
    public ItemDto getItem(@PathVariable Long itemId) {
        log.info("Get Item with id: {}", itemId);
        return itemService.findById(itemId);
    }

    @GetMapping
    public Collection<ItemDto> getUserItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Get User({}) items", userId);
        return itemService.findByUser(userId);
    }

    @GetMapping("search")
    public Collection<ItemDto> searchItemsByText(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @RequestParam(defaultValue = "") String text) {
        log.info("Search Items by Text: {}", text);
        return itemService.findByText(text);
    }
}
