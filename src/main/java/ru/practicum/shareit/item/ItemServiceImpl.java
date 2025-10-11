package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingSmallDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

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
    public ItemWithBookingDto getItem(long id, long userId) {
        Item item = itemRepository.findById(id).orElseThrow(() -> {
            log.warn("Unable to get item. Item not found.");
            return new NotFoundException("Item not found.");
        });
        BookingSmallDto lastBookingSmallDto = null;
        BookingSmallDto nextBookingSmallDto = null;
        if (item.getOwner().getId().equals(userId)) {
            Optional<Booking> lastBooking = bookingRepository
                    .findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(item.getId(),
                            BookingStatus.APPROVED, LocalDateTime.now());
            if (lastBooking.isPresent()) {
                lastBookingSmallDto = BookingMapper.toBookingSmallDto(lastBooking.get());
            }
            Optional<Booking> nextBooking = bookingRepository
                    .findFirstByItemIdAndStatusAndStartAfterOrderByStart(item.getId(),
                            BookingStatus.APPROVED, LocalDateTime.now());
            if (nextBooking.isPresent()) {
                nextBookingSmallDto = BookingMapper.toBookingSmallDto(nextBooking.get());
            }
        }
        ItemWithBookingDto itemWithBookingDto = ItemMapper.toItemWithBookingDto(item, lastBookingSmallDto, nextBookingSmallDto);
        itemWithBookingDto.setComments(commentRepository.findAllByItemIdOrderByCreatedDesc(item.getId())
                .stream().map(CommentMapper::toCommentDto).toList());
        return itemWithBookingDto;
    }

    @Override
    public List<ItemWithBookingDto> getItems(long userId) {
        List<Item> items = itemRepository.findAllByOwnerIdOrderById(userId);
        if (items.isEmpty()) {
            return List.of();
        }
        List<Long> itemsIds = items.stream().map(Item::getId).toList();

        List<Booking> past = bookingRepository.findPastForItems(itemsIds, LocalDateTime.now());
        List<Booking> future = bookingRepository.findFutureForItems(itemsIds, LocalDateTime.now());

        Map<Long, List<Comment>> commentMap = commentRepository.findAllByItemIdsOrderByCreatedDesc(itemsIds)
                .stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId()));

        Map<Long, Booking> lastByItem = new HashMap<>();
        for (Booking booking : past) {
            lastByItem.putIfAbsent(booking.getItem().getId(), booking);
        }
        Map<Long, Booking> nextByItem = new HashMap<>();
        for (Booking booking : future) {
            nextByItem.putIfAbsent(booking.getItem().getId(), booking);
        }
        return items
                .stream()
                .map(item -> {
                    BookingSmallDto lastBooking = new BookingSmallDto();
                    BookingSmallDto nextBooking = new BookingSmallDto();
                    if (lastByItem.get(item.getId()) != null) {
                        lastBooking = BookingMapper.toBookingSmallDto(lastByItem.get(item.getId()));
                    }
                    if (nextByItem.get(item.getId()) != null) {
                        nextBooking = BookingMapper.toBookingSmallDto(nextByItem.get(item.getId()));
                    }
                    ItemWithBookingDto itemWithBookingDto = ItemMapper.toItemWithBookingDto(item, lastBooking, nextBooking);
                    itemWithBookingDto.setComments(commentMap.getOrDefault(item.getId(), Collections.emptyList())
                            .stream().map(CommentMapper::toCommentDto).toList());
                    return itemWithBookingDto;
                }).toList();
    }


    @Override
    public List<ItemDto> searchItems(String text, long userId) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return itemRepository.search(text).stream().map(ItemMapper::toItemDto).toList();
    }

    @Override
    @Transactional
    public CommentDto createComment(CommentDto commentDto, long authorId, long itemId) {
        User author = userRepository.findById(authorId).orElseThrow(() -> {
            log.warn("Unable to create comment. User not found.");
            return new NotFoundException("User not found.");
        });
        Item item = itemRepository.findById(itemId).orElseThrow(() -> {
            log.warn("Unable to create comment. Item not found.");
            return new NotFoundException("Item not found.");
        });
        bookingRepository.findByBookerIdAndItemIdAndStatusAndEndBefore(authorId, itemId, BookingStatus.APPROVED
                , LocalDateTime.now()).orElseThrow(() -> {
            log.warn("Unable to create comment. The author did not booking this item.");
            return new BadRequestException("The author did not booking this item.");
        });
        Comment comment = commentRepository.save(CommentMapper.toComment(commentDto, author, item));
        return CommentMapper.toCommentDto(comment);
    }
}
