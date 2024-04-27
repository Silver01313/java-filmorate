package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Data
@Builder(toBuilder = true)
public class Mpa {
    @NonNull
    private final Integer id;
    private String name;
}
