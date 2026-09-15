package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;


@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserDto createUser(@Valid @RequestBody CreateUserDto createUserDto) {
        log.info("Create User: {}", createUserDto);
        return userService.create(createUserDto);
    }

    @PatchMapping("{userId}")
    public UserDto updateUser(@PathVariable Long userId, @Valid @RequestBody UserDto userDto) {
        log.info("Update User: {}", userDto);
        return userService.update(userId, userDto);
    }

    @DeleteMapping("{userId}")
    public void deleteUser(@PathVariable Long userId) {
        log.info("Delete User: {}", userId);
        userService.delete(userId);
    }

    @GetMapping("{userId}")
    public UserDto getUser(@PathVariable Long userId) {
        log.info("Get User: {}", userId);
        return userService.findById(userId);
    }

    @GetMapping
    public Collection<UserDto> findAllUsers() {
        log.info("Find All Users");
        return userService.findAll();
    }
}
