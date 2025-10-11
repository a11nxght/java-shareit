package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.DuplicatedDataException;
import ru.practicum.shareit.exceptions.NotFoundException;
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
@Transactional(readOnly = true)
class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto create(NewUserRequest request) {
        Optional<User> alreadyExistUser = userRepository.findByEmail(request.getEmail());
        if (alreadyExistUser.isPresent()) {
            log.warn("Unable to create user.This email is already in use.");
            throw new DuplicatedDataException("This email is already in use.");
        }
        User user = UserMapper.mapToUser(request);
        user = userRepository.save(user);
        return UserMapper.mapToUserDto(user);
    }

    @Override
    @Transactional
    public UserDto update(UpdateUserRequest request, long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.warn("Unable to update user.User not found");
            return new NotFoundException("User not found");
        });
        if (request.hasEmail()) {
            Optional<User> alreadyExistUser = userRepository.findByEmail(request.getEmail());
            if (alreadyExistUser.isPresent()) {
                log.warn("Unable to update user.This email is already in use.");
                throw new DuplicatedDataException("This email is already in use.");
            }
        }
        UserMapper.updateUserFields(user, request);
        userRepository.save(user);
        return UserMapper.mapToUserDto(user);
    }

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUser(long id) {
        return UserMapper
                .mapToUserDto(userRepository.findById(id).orElseThrow(() -> {
                    log.warn("Unable to get user.User not found");
                    return new NotFoundException("User not found");
                }));
    }

    @Override
    @Transactional
    public void deleteUser(long id) {
        userRepository.deleteById(id);
    }
}
