package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    User add(User user);

    User get(long id);

    List<User> getAll();

    User update(User user);

    void delete(long id);

    Optional<User> findByEmail(String email);
}
