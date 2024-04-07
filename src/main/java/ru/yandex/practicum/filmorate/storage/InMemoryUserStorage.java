package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();
    private Integer userId = 0;

    @Override
    public List<User> findAll() {
        log.debug("Список всех пользователей");
        return new ArrayList<>(users.values());
    }

    public User create(User user) throws ValidationException {
        int spaceIndex = user.getLogin().indexOf(" ");
        int atIndex = user.getEmail().indexOf("@");

        if (user.getId() != null) {
            log.warn("Такой пользователь уже существует");
            throw new ValidationException("Такой пользователь уже существует");
        }
        if (user.getName() == null) {
            user = user.toBuilder().name(user.getLogin()).build();
        }
        if (atIndex < 0 || user.getEmail().isBlank()) {
            log.warn("Не корректный электронный адрес");
            throw new ValidationException("Электронный адрес не может быть пустым и должен содержать @");
        }
        if (spaceIndex >= 0 || user.getLogin().isBlank()) {
            log.warn("Не корректный логин");
            throw new ValidationException("Логин не может быть пустым и не должен содержать пробелы");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Не корректная дата рождения");
            throw new ValidationException("Не корректная дата рождения");
        }
        user = user.toBuilder().id(generateId()).build();
        users.put(user.getId(), user);
        log.debug("Вы добавили пользователя : {}", user);
        return user;
    }

    public User update(User user) throws ValidationException {
        int spaceIndex = user.getLogin().indexOf(" ");
        int atIndex = user.getEmail().indexOf("@");

        if (!users.containsKey(user.getId())) {
            log.warn("Такого пользователя не существует");
            throw new NotFoundException("Такого пользователя не существует");
        }
        if (user.getName() == null) {
            user = user.toBuilder().name(user.getLogin()).build();
        }
        if (atIndex < 0 || user.getEmail().isBlank()) {
            log.warn("Не корректный электронный адрес");
            throw new ValidationException("Электронный адрес не может быть пустым и должен содержать @");
        }
        if (spaceIndex >= 0 || user.getLogin().isBlank()) {
            log.warn("Не корректный логин");
            throw new ValidationException("Логин не может быть пустым и не должен содержать пробелы");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Не корректная дата рождения");
            throw new ValidationException("Не корректная дата рождения");
        }
        log.debug("Вы добавили пользователя : {}", user);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User getUser(Integer id) {
        if (!users.containsKey(id)) {
            log.warn("Такого пользователя не существует");
            throw new NotFoundException("Такого пользователя не существует");
        }
        return users.get(id);
    }

    private Integer generateId() {
        return ++userId;
    }
}
