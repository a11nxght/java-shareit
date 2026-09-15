package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exeptions.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
public class FakeUserRepository implements UserRepository {

    private static final Map<Long, User> users = new HashMap<>();
    private static long lastId = 0;

    @Override
    public User save(User user) {
        if (isEmailUniq(user.getEmail())) {
            user.setId(++lastId);
            users.put(user.getId(), user);
            return user;
        }  else {
            throw new IllegalStateException("Email already in use");
        }
    }

    @Override
    public User update(User user) {
        User updatedUser = users.get(user.getId());
        if (updatedUser == null) {
            throw new NotFoundException("User not found");
        }
        if (user.getName() != null) {
            updatedUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            if (isEmailUniq(user.getEmail())) {
                updatedUser.setEmail(user.getEmail());
            } else {
                throw new IllegalStateException("Email already in use");
            }
        }
        return users.get(user.getId());

    }

    @Override
    public void delete(Long id) {
        users.remove(id);
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public List<User> findAll() {
        return users.values().stream().toList();
    }

    private boolean isEmailUniq(String email) {
        return users.values().stream().noneMatch(u -> u.getEmail().equals(email));
    }
}
