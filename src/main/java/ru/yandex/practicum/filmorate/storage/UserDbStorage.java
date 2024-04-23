package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component
@Primary
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
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

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");

        Map<String, Object> values = new HashMap<>();
        values.put("email", user.getEmail());
        values.put("login", user.getLogin());
        values.put("name", user.getName());
        values.put("birthday", user.getBirthday());

        int userId = simpleJdbcInsert.executeAndReturnKey(values).intValue();
        User newUser = user.toBuilder()
                .id(userId)
                .friends(new HashSet<>(getUserFriends(userId)))
                .build();

        log.debug("Вы добавили пользователя : {}", user);
        return newUser;
    }

    @Override
    public User update(User user) throws ValidationException {
        SqlRowSet filmId = jdbcTemplate.queryForRowSet("select ID from users  where ID = ?", user.getId());
        if (!filmId.next()) {
            throw new NotFoundException("такого пользователя не существует");
        }
        int spaceIndex = user.getLogin().indexOf(" ");
        int atIndex = user.getEmail().indexOf("@");

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
        String sql = "UPDATE users SET  email = ?, login = ?, name = ?, birthday = ?  WHERE id = ?";
        jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(),
                user.getId());
        jdbcTemplate.update("DELETE FROM FRIENDSHIP WHERE USER_ID = ?", user.getId());
        for (Integer i : user.getFriends()) {
            jdbcTemplate.update("INSERT INTO FRIENDSHIP (USER_ID, FRIEND_ID) VALUES(?,?)", user.getId(), i);
        }
        log.debug("Вы обновили пользователя : {}", user);
        return user;
    }

    @Override
    public List<User> findAll() {
        SqlRowSet sql = jdbcTemplate.queryForRowSet("SELECT * FROM users");
        List<User> userList = new ArrayList<>();
        while (sql.next()) {
            userList.add(User.builder()
                    .id(sql.getInt("id"))
                    .email(Objects.requireNonNull(sql.getString("email")))
                    .login(Objects.requireNonNull(sql.getString("login")))
                    .name(sql.getString("name"))
                    .birthday(Objects.requireNonNull(sql.getDate("birthday")).toLocalDate())
                    .friends(getUserFriends(sql.getInt("id")))
                    .build());
        }
        log.info("Список пользователей");
        return userList;
    }

    @Override
    public User getUser(Integer id) {
        SqlRowSet sql = jdbcTemplate.queryForRowSet("SELECT * FROM users WHERE id = ?", id);
        if (sql.next()) {
            User user = User.builder()
                    .id(id)
                    .email(Objects.requireNonNull(sql.getString("email")))
                    .login(Objects.requireNonNull(sql.getString("login")))
                    .name(sql.getString("name"))
                    .birthday(Objects.requireNonNull(sql.getDate("birthday")).toLocalDate())
                    .friends(getUserFriends(id))
                    .build();

            log.info("Найден пользователь: {} ", user);
            return user;
        } else {
            log.warn("Такого пользователя не существует");
            throw new NotFoundException("Такого пользователя не существует");
        }
    }

    @Override
    public Integer addFriend(Integer userId, Integer friendId) {
        try {
            String sql = "INSERT INTO FRIENDSHIP (USER_ID, FRIEND_ID) values(?,?)";
            jdbcTemplate.update(sql, userId, friendId);
            return friendId;
        } catch (RuntimeException e) {
            log.warn("Такого пользователя не существует");
            throw new NotFoundException("Такого пользователя не существует");
        }
    }

    @Override
    public Integer deleteFriend(Integer userId, Integer friendId) {
        getUser(userId);
        getUser(friendId);
        String sql = "DELETE FROM FRIENDSHIP WHERE (USER_ID = ? AND FRIEND_ID = ?)";
        jdbcTemplate.update(sql, userId, friendId);
        return friendId;
    }

    public Set<Integer> getUserFriends(Integer userId) {
        Set<Integer> userFriends = new HashSet<>();
        SqlRowSet sql = jdbcTemplate.queryForRowSet("SELECT FRIEND_ID FROM FRIENDSHIP WHERE USER_ID = ?", userId);
        while (sql.next()) {
            userFriends.add(sql.getInt("FRIEND_ID"));
        }
        return userFriends;
    }
}
