package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class MpaDbStorage implements  MpaStorage{

    private final JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Mpa> getMpa() {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("SELECT * FROM RATING r ");
        List<Mpa> mpaList = new ArrayList<>();
        while (filmRows.next()) {
            mpaList.add(Mpa.builder()
                    .id(filmRows.getInt("id"))
                    .name(Objects.requireNonNull(filmRows.getString("name")))
                    .build());
        }
        return mpaList;
    }

    @Override
    public Mpa getMpaById(Integer id) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("SELECT * FROM RATING r WHERE id = ? ", id);
        if (filmRows.next()) {
            return Mpa.builder().id(filmRows.getInt("id"))
                    .name(Objects.requireNonNull(filmRows.getString("name")))
                    .build();

        } else {
            log.warn("Такого MPA не существует");
            throw new NotFoundException("Такого MPA не существует");
        }
    }
}
