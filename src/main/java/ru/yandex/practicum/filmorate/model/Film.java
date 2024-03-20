package ru.yandex.practicum.filmorate.model;

import lombok.*;

import java.time.LocalDate;

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
}
