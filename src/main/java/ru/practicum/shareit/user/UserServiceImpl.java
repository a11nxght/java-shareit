package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.DuplicatedDataException;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Override
    public UserDto create(NewUserRequest request) {
        Optional<User> alreadyExistUser = userStorage.findByEmail(request.getEmail());
        if (alreadyExistUser.isPresent()) {
            throw new DuplicatedDataException("Данный имейл уже используется");
        }
        User user = UserMapper.mapToUser(request);
        user = userStorage.add(user);
        return UserMapper.mapToUserDto(user);
    }

    @Override
    public UserDto update(UpdateUserRequest request, long userId) {
        User user = userStorage.get(userId);
        if (request.hasEmail()) {
            Optional<User> alreadyExistUser = userStorage.findByEmail(request.getEmail());
            if (alreadyExistUser.isPresent()) {
                throw new DuplicatedDataException("Данный имейл уже используется");
            }
        }
        User updatedUser = UserMapper.updateUserFields(user, request);
        updatedUser = userStorage.update(updatedUser);
        return UserMapper.mapToUserDto(updatedUser);
    }

    @Override
    public List<UserDto> findAll() {
        return userStorage.getAll()
                .stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUser(long id) {
        return UserMapper.mapToUserDto(userStorage.get(id));
    }

    @Override
    public void deleteUser(long id) {
        userStorage.delete(id);
    }
}