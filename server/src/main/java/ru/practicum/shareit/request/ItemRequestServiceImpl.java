package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto) {
        User requestor = getUserIfExist(userId, "Unable to create item request. User not found.");
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, requestor);
        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest), List.of());
    }

    @Override
    public List<ItemRequestDto> getOwn(Long userId) {
        getUserIfExist(userId, "Unable to get item requests. User not found.");
        Sort newestFirst = Sort.by(Sort.Direction.DESC, "created");
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestorId(userId, newestFirst);
        return itemRequests.stream().map(itemRequest ->
                ItemRequestMapper.toItemRequestDto(itemRequest, itemRepository.findAllByRequestId(userId))).toList();
    }

    @Override
    public List<ItemRequestDto> getAll(Long userId) {
        getUserIfExist(userId, "Unable to get all item requests. User not found.");
        Sort newestFirst = Sort.by(Sort.Direction.DESC, "created");
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestorIdNot(userId, newestFirst);
        return itemRequests.stream().map(itemRequest ->
                ItemRequestMapper.toItemRequestDto(itemRequest, itemRepository.findAllByRequestId(userId))).toList();
    }

    @Override
    public ItemRequestDto getById(Long userId, Long requestId) {
        getUserIfExist(userId, "Unable to get item requests by Id. User not found.");
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(() -> {
            log.warn("Unable to get item requests by Id. Item request not found.");
            return new NotFoundException("Item request not found.");
        });
        return ItemRequestMapper.toItemRequestDto(itemRequest, itemRepository.findAllByRequestId(requestId));
    }

    private User getUserIfExist(Long userId, String warnMessage) {
        return userRepository.findById(userId).orElseThrow(() -> {
            log.warn(warnMessage);
            return new NotFoundException("User not found.");
        });
    }
}
