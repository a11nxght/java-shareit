package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
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
        User user = UserMapper.mapToUser(request);
        try {
            user = userRepository.save(user);
            return UserMapper.mapToUserDto(user);
        } catch (DataIntegrityViolationException exception) {
            log.warn("Unable to create user. Email {} is already in use.", request.getEmail());
            throw new DuplicatedDataException("This email is already in use.");
        }
    }

    @Override
    @Transactional
    public UserDto update(UpdateUserRequest request, long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.warn("Unable to update user.User not found");
            return new NotFoundException("User not found");
        });
        UserMapper.updateUserFields(user, request);
        try {
            userRepository.save(user);
            return UserMapper.mapToUserDto(user);
        } catch (DataIntegrityViolationException exception) {
            log.warn("Unable to update user. Email {} is already in use.", request.getEmail());
            throw new DuplicatedDataException("This email is already in use.");
        }
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
