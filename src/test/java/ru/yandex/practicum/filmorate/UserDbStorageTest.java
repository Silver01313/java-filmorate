package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbStorageTest {
    private final JdbcTemplate jdbcTemplate;

    @Test
    public void testFindUserById() throws ValidationException {
        User newUser = User.builder()
                .email("user@email.ru")
                .login("vanya123")
                .name("Ivan")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        newUser = userStorage.create(newUser);

        User savedUser = userStorage.getUser(newUser.getId());

        assertThat(savedUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(newUser);
    }

    @Test
    public void testCreateUser() throws ValidationException {
        User newUser = User.builder()
                .email("user@email.ru")
                .login("vanya123")
                .name("Ivan")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);

        assertThat(userStorage.findAll().size())
                .isEqualTo(0);

        newUser = userStorage.create(newUser);

        assertThat(userStorage.findAll().size())
                .isEqualTo(1);

        assertThat(userStorage.getUser(newUser.getId()))
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(newUser);
    }

    @Test
    public void testUpdateUserById() throws ValidationException {
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        User newUser = User.builder()
                .email("user@email.ru")
                .login("vanya123")
                .name("Ivan")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        newUser = userStorage.create(newUser);

        User newUser2 = User.builder()
                .email("user2@email.ru")
                .login("vanya1231")
                .name("Ivanus")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        newUser2 = userStorage.create(newUser2);

         userStorage.update(newUser2);

        User savedUser = userStorage.getUser(newUser2.getId());

        assertThat(savedUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(newUser2);
    }

    @Test
    public void testFindUserList() throws ValidationException {
        User newUser = User.builder()
                .email("user@email.ru")
                .login("vanya123")
                .name("Ivan")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        newUser = userStorage.create(newUser);

        User newUser2 = User.builder()
                .email("us2er@email.ru")
                .login("vaneya123")
                .name("Ivdan")
                .birthday(LocalDate.of(1995, 1, 1))
                .build();
        newUser2 = userStorage.create(newUser2);

        User savedUser = userStorage.getUser(newUser.getId());
        User savedUser2 = userStorage.getUser(newUser2.getId());

        assertThat(savedUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(userStorage.findAll().get(0));

        assertThat(savedUser2)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(userStorage.findAll().get(1));
    }

    @Test
    public void testAddAndDeleteFriend() throws ValidationException {
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        User newUser = User.builder()
                .email("user@email.ru")
                .login("vanya123")
                .name("Ivan")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        newUser = userStorage.create(newUser);

        User newUser2 = User.builder()
                .email("user2@email.ru")
                .login("vanya1231")
                .name("Ivanus")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        newUser2 = userStorage.create(newUser2);

        assertThat(newUser.getFriends().size())
                .isEqualTo(0);

        userStorage.addFriend(newUser.getId(),newUser2.getId());

        newUser = userStorage.getUser(newUser.getId());

        assertThat(newUser.getFriends().size())
                .isEqualTo(1);

        userStorage.deleteFriend(newUser.getId(),newUser2.getId());

        newUser = userStorage.getUser(newUser.getId());

        assertThat(newUser.getFriends().size())
                .isEqualTo(0);
    }

}