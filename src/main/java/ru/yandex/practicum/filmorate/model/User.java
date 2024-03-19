package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder(toBuilder = true)
public class User {
    Integer id;
    @NonNull
    String email;
    @NonNull
    String login;
    String name;
    @NonNull
    LocalDate birthday;
}
