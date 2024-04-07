package ru.yandex.practicum.filmorate.model;

import lombok.*;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Jacksonized
@Data
@Builder(toBuilder = true)
public class Film {
    private final Integer id;
    @NonNull
    private final String name;
    @NonNull
    private final String description;
    @NonNull
    private final LocalDate releaseDate;
    @NonNull
    private final Integer duration;
    @Builder.Default
    private final Set<Integer> likes = new HashSet<>();
}
