package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exeption.AlreadyExistException;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserValidationTest {
    UserController userController;
    User user;

    @BeforeEach
    public void createUserAndController() {
        userController = new UserController();
        user = User.builder()
                .email("@")
                .login("a")
                .name("b")
                .birthday(LocalDate.now().minusYears(20))
                .build();
    }

    @Test
    public void shouldThrowExceptionIfIncorrectEmail() throws ValidationException {
        User user1 = user.toBuilder().email("").build();
        User user2 = user.toBuilder().email("a").build();

        ValidationException exception = assertThrows(ValidationException.class, () -> userController.create(user1));
        assertEquals("Электронный адрес не может быть пустым и должен содержать @", exception.getMessage());
        ValidationException exception2 = assertThrows(ValidationException.class, () -> userController.create(user2));
        assertEquals("Электронный адрес не может быть пустым и должен содержать @", exception2.getMessage());

        user = userController.create(user);
        User user3 = user.toBuilder().email("").build();
        User user4 = user.toBuilder().email("a").build();

        ValidationException exception3 = assertThrows(ValidationException.class, () -> userController.update(user3));
        assertEquals("Электронный адрес не может быть пустым и должен содержать @", exception3.getMessage());
        ValidationException exception4 = assertThrows(ValidationException.class, () -> userController.update(user4));
        assertEquals("Электронный адрес не может быть пустым и должен содержать @", exception4.getMessage());
    }

    @Test
    public void shouldThrowExceptionIfIncorrectLogin() throws ValidationException {
        User user1 = user.toBuilder().login("").build();
        User user2 = user.toBuilder().login("a b").build();

        ValidationException exception = assertThrows(ValidationException.class, () -> userController.create(user1));
        assertEquals("Логин не может быть пустым и не должен содержать пробелы", exception.getMessage());
        ValidationException exception2 = assertThrows(ValidationException.class, () -> userController.create(user2));
        assertEquals("Логин не может быть пустым и не должен содержать пробелы", exception2.getMessage());

        user = userController.create(user);
        User user3 = user.toBuilder().login("").build();
        User user4 = user.toBuilder().login("a b").build();

        ValidationException exception3 = assertThrows(ValidationException.class, () -> userController.update(user3));
        assertEquals("Логин не может быть пустым и не должен содержать пробелы", exception3.getMessage());
        ValidationException exception4 = assertThrows(ValidationException.class, () -> userController.update(user4));
        assertEquals("Логин не может быть пустым и не должен содержать пробелы", exception4.getMessage());
    }

    @Test
    public void shouldUseLoginIfNameIsEmpty() throws ValidationException {
        user = user.toBuilder().name(null).build();
        User user1 = userController.create(user);

        assertEquals(user1.getLogin(), user1.getName(), "Имя и логин не совпадают");

        User user3 = userController.update(user1);

        assertEquals(user3.getLogin(), user3.getName(), "Имя и логин не совпадают");
    }

    @Test
    public void shouldThrowExceptionIfDateOfBirthdayInFuture() throws ValidationException {
        User user1 = user.toBuilder().birthday(LocalDate.now().plusDays(1)).build();

        ValidationException exception = assertThrows(ValidationException.class, () -> userController.create(user1));
        assertEquals("Не корректная дата рождения", exception.getMessage());

        user = userController.create(user);
        User user2 = user.toBuilder().birthday(LocalDate.now().plusDays(1)).build();

        ValidationException exception2 = assertThrows(ValidationException.class, () -> userController.update(user2));
        assertEquals("Не корректная дата рождения", exception2.getMessage());
    }

    @Test
    public void shouldThrowExceptionIfUserAlreadyExist() {
        user = user.toBuilder().id(1).build();

        AlreadyExistException exception = assertThrows(AlreadyExistException.class, () -> userController.create(user));
        assertEquals("Такой пользователь уже существует", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionIfUpdateNonexistentUser() {
        ValidationException exception = assertThrows(ValidationException.class, () -> userController.update(user));
        assertEquals("Такого пользователя не существует", exception.getMessage());
    }

}
