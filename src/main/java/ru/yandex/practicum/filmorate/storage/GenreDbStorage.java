package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Genre> getGenres() {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("SELECT * FROM GENRE g ");
        List<Genre> genreList = new ArrayList<>();
        while (filmRows.next()) {
            genreList.add(Genre.builder()
                    .id(filmRows.getInt("id"))
                    .name(Objects.requireNonNull(filmRows.getString("name")))
                    .build());
        }
        return genreList;
    }

    @Override
    public Genre getGenreById(Integer id) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("SELECT * FROM GENRE g WHERE id = ? ", id);
        if (filmRows.next()) {
            return Genre.builder()
                    .id(filmRows.getInt("id"))
                    .name(Objects.requireNonNull(filmRows.getString("name")))
                    .build();
        } else {
            log.warn("Такого жанра не существует");
            throw new NotFoundException("Такого жанра не существует");
        }
    }
}
