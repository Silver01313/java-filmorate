package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
public class User {
    private final Integer id;
    @NonNull
    private final String email;
    @NonNull
    private final String login;
    private final String name;
    @NonNull
    private final LocalDate birthday;
}
