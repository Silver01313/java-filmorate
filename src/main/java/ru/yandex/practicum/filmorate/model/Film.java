package ru.yandex.practicum.filmorate.model;

import lombok.*;

import java.time.LocalDate;

@Value
@Builder(toBuilder = true)
public class Film {
    Integer id;
    @NonNull
    String name;
    @NonNull
    String description;
    @NonNull
    LocalDate releaseDate;
    @NonNull
    Integer duration;
}
