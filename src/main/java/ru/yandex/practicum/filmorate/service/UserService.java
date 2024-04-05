package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Getter
@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Integer addFriend(Integer id, Integer friendId) {
        try {
            Set<Integer> friends1 = userStorage.getUser(id).getFriends();
            Set<Integer> friends2 = userStorage.getUser(friendId).getFriends();
            friends1.add(friendId);
            friends2.add(id);
        } catch (NotFoundException e) {
            log.warn("Такого пользователя не существует");
            throw new NotFoundException("Такого пользователя не существует");
        }
        return friendId;
    }

    public Integer deleteFriend(Integer id, Integer friendId) {
        Set<Integer> friends1 = userStorage.getUser(id).getFriends();
        Set<Integer> friends2 = userStorage.getUser(friendId).getFriends();

        friends1.remove(friendId);
        friends2.remove(id);
        log.debug("Друг удален");
        return friendId;
    }

    public List<User> getFriends(Integer id) {
        try {
            return userStorage.getUser(id).getFriends().stream()
                    .map(userStorage::getUser)
                    .collect(Collectors.toList());
        } catch (NotFoundException e) {
            log.warn("Такого пользователя не существует");
            throw new NotFoundException("Такого пользователя не существует");
        }
    }

    public List<User> getCommonFriends(Integer id, Integer otherId) {
        try {
            List<User> commonFriends = new ArrayList<>();
            Set<Integer> user1Friends = userStorage.getUser(id).getFriends();
            Set<Integer> user2Friends = userStorage.getUser(otherId).getFriends();

            for (Integer userId : user1Friends) {
                if (user2Friends.contains(userId)) {
                    commonFriends.add(userStorage.getUser(userId));
                }
            }
            log.debug("Список общих друзей");
            return commonFriends;
        } catch (NotFoundException e) {
            log.warn("Такого пользователя не существует");
            throw new NotFoundException("Такого пользователя не существует");
        }
    }
}
