package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

interface UserService {

    UserDto create(NewUserRequest request);

    UserDto update(UpdateUserRequest request, long userId);

    List<UserDto> findAll();

    UserDto getUser(long id);

    void deleteUser(long id);
}
