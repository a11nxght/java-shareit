package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto create(CreateUserDto createUserDto);

    UserDto update(Long userId, UserDto userDto);

    UserDto findById(Long id);

    void delete(Long id);

    List<UserDto> findAll();
}
