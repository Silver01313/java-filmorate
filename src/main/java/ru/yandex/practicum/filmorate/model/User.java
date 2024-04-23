package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Jacksonized
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
    @Builder.Default
    private final Set<Integer> friends = new HashSet<>();
}
