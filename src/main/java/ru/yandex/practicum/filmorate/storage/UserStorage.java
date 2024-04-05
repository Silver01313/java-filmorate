package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Component
public interface UserStorage {
    User create(User user) throws ValidationException;

    User update(User user) throws ValidationException;

    List<User> findAll();

    User getUser(Integer id);
}
