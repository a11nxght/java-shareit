package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeptions.NotFoundException;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto create(CreateUserDto createUserDto) {
        User user = UserMapper.toUser(createUserDto);
        User createdUser = userRepository.save(user);
        log.info("Created User with id: {}", createdUser.getId());
        return UserMapper.toUserDto(createdUser);
    }

    @Override
    public UserDto update(Long userId, UserDto userDto) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        User user = UserMapper.toUser(userDto);
        user.setId(userId);
        User updatedUser = userRepository.update(user);
        log.info("Updated User with id: {}", updatedUser.getId());
        return UserMapper.toUserDto(updatedUser);
    }

    @Override
    public UserDto findById(Long id) {
        return UserMapper.toUserDto(userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found")));
    }

    @Override
    public void delete(Long id) {
        userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
        userRepository.delete(id);
        log.info("Deleted User with id: {}", id);
    }

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll().stream().map(UserMapper::toUserDto).toList();
    }
}
