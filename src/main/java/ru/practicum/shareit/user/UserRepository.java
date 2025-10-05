package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class UserRepository implements UserStorage {
    private long taskId = 0;
    private final HashMap<Long, User> users = new HashMap<>();

    @Override
    public User add(User user) {
        user.setId(++taskId);
        users.put(taskId, user);
        return user;
    }

    @Override
    public User get(long id) {
        if (users.containsKey(id)) {
            return users.get(id);
        }
        log.warn("При запросе данных пользователя возникла ошибка: Пользователь c id({}) не найден", id);
        throw new NotFoundException("Пользователь " + id + " не найден");
    }

    @Override
    public List<User> getAll() {
        return users.values().stream().toList();
    }

    @Override
    public User update(User user) {
        if (users.containsKey(user.getId())) {
            users.replace(user.getId(), user);
            return user;
        } else {
            log.warn("При обновлении данных пользователя возникла ошибка: Пользователь c id({}) не найден", user.getId());
            throw new NotFoundException("Пользователь " + user.getId() + " не найден");
        }
    }

    @Override
    public void delete(long id) {
        if (users.containsKey(id)) {
            users.remove(id);
        } else {
            log.warn("При удалении данных пользователя возникла ошибка: Пользователь c id({}) не найден", id);

            throw new NotFoundException("Пользователь " + id + " не найден");
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return users.values().stream().filter(user -> user.getEmail().equals(email)).findFirst();
    }
}