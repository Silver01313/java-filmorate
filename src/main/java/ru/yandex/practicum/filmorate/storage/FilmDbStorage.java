package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film create(Film film) throws ValidationException, InvocationTargetException {
        if (film.getId() != null) {
            log.warn("Такой фильм уже существует");
            throw new ValidationException("Этот фильм уже зарегистрирован");
        }
        if (film.getName().isBlank()) {
            log.warn("Не корректное название");
            throw new ValidationException("Название не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            log.warn("Не корректное описание");
            throw new ValidationException("Описание должно быть не длиннее 200 символов");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Не корректная дата выхода");
            throw new ValidationException("Дата выхода должна быть не ранее 28.12.1895г.");
        }
        if (film.getDuration() <= 0) {
            log.warn("Не корректная продолжительность");
            throw new ValidationException("Продолжительность фильма должна быть больше 0");
        }
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("film")
                .usingGeneratedKeyColumns("id");

        Map<String, Object> values = new HashMap<>();
        values.put("name", film.getName());
        values.put("description", film.getDescription());
        values.put("releaseDate", film.getReleaseDate());
        values.put("duration", film.getDuration());
        if (film.getMpa() != null) {
            if (film.getMpa().getId() > 5) {
                throw new ValidationException("id рейтинга не может быть выше 5");
            }
            values.put("filmRating_id", film.getMpa().getId());
        }

        int filmId = simpleJdbcInsert.executeAndReturnKey(values).intValue();
        Film newFilm = film.toBuilder().id(filmId).build();

        if (!newFilm.getGenres().isEmpty()) {
            for (Genre g : newFilm.getGenres()) {
                if (g.getId() > 6) {
                    throw new ValidationException("id жанра не может быть выше 6");
                }
            }

            String sqlGenre = "INSERT INTO film_genre (film_id, genre_id) VALUES (?,?)";
            for (Genre g : newFilm.getGenres()) {
                jdbcTemplate.update(sqlGenre, filmId, g.getId());
            }
        }

        if (!newFilm.getLikes().isEmpty()) {
            String sqlLikes = "INSERT INTO film_likes (film_id, user_id) VALUES (?,?)";
            for (Integer l : newFilm.getLikes()) {
                jdbcTemplate.update(sqlLikes, filmId, l);
            }
        }
        log.debug("Вы добавили фильм : {}", newFilm);
        return newFilm;
    }

    @Override
    public Film update(@RequestBody Film film) throws ValidationException {
        SqlRowSet filmId = jdbcTemplate.queryForRowSet("select ID from film f where f.ID = ?", film.getId());
        if (!filmId.next()) {
            throw new NotFoundException("такого фильма не существует");
        }
        if (film.getName().isBlank()) {
            log.warn("Не корректное название");
            throw new ValidationException("Название не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            log.warn("Не корректное описание");
            throw new ValidationException("Описание должно быть не длиннее 200 символов");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Не корректная дата выхода");
            throw new ValidationException("Дата выхода должна быть не ранее 28.12.1895г.");
        }
        if (film.getDuration() <= 0) {
            log.warn("Не корректная продолжительность");
            throw new ValidationException("Продолжительность фильма должна быть больше 0");
        }
        if (film.getMpa() != null) {
            String sql = "UPDATE film SET  name = ?, description = ?, releaseDate = ?, duration = ?,"
                    + " filmRating_id = ? WHERE id = ?";
            jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(),
                    film.getDuration(), film.getMpa().getId(), film.getId());
        } else {
            String sql = "UPDATE film SET name = ?, description = ?, releaseDate = ?, duration = ? "
                    + "WHERE id = ?";
            jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(),
                    film.getDuration(), film.getId());
        }
        log.debug("Вы обновили фильм : {}", film);
        return film;
    }

    @Override
    public List<Film> findAll() {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from film");
        List<Film> films = new ArrayList<>();
        while (filmRows.next()) {
            int id = filmRows.getInt("id");
            Film film = Film.builder()
                    .id(id).
                    name(Objects.requireNonNull(filmRows.getString("name")))
                    .description(Objects.requireNonNull(filmRows.getString("description")))
                    .releaseDate(Objects.requireNonNull(filmRows.getDate("releaseDate"))
                    .toLocalDate()).duration(filmRows.getInt("duration"))
                    .mpa(getMpaByFilm(id)).genres(getFilmGenres(id)).likes(getFilmLikes(id))
                    .build();
            films.add(film);
        }
        log.debug("Список всех фильмов");
        return films;
    }

    @Override
    public Film getFilm(Integer id) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from film where id = ?", id);

        if (filmRows.next()) {
            Film film = Film.builder()
                    .id(id)
                    .name(Objects.requireNonNull(filmRows.getString("name")))
                    .description(Objects.requireNonNull(filmRows.getString("description")))
                    .releaseDate(Objects.requireNonNull(filmRows.getDate("releaseDate"))
                    .toLocalDate()).duration(filmRows.getInt("duration"))
                    .mpa(getMpaByFilm(id)).genres(getFilmGenres(id)).likes(getFilmLikes(id))
                    .build();

            log.info("Найден фильм: {} ", film);
            return film;
        } else {
            log.warn("Такого фильма не существует");
            throw new NotFoundException("Такого фильма не существует");
        }
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

    public Mpa getMpaByFilm(int filmId) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("SELECT f.FILMRATING_ID, r.NAME  FROM FILM f "
                + "INNER JOIN RATING r ON F.FILMRATING_ID  = r.ID WHERE f.ID = ?", filmId);
        if (filmRows.next()) {
            return Mpa.builder()
                    .id(filmRows.getInt("filmRating_id"))
                    .name(filmRows.getString("name"))
                    .build();
        } else {
            return null;
        }
    }

    public List<Genre> getFilmGenres(int filmId) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("SELECT g.ID, g.NAME  FROM FILM_GENRE fg INNER JOIN"
                + " GENRE g ON fg.GENRE_ID = g.ID  WHERE FILM_ID  = ? GROUP BY ID ", filmId);
        List<Genre> genreList = new ArrayList<>();
        while (filmRows.next()) {
            genreList.add(Genre.builder()
                    .id(filmRows.getInt("id"))
                    .name(filmRows.getString("name"))
                    .build());
        }
        return genreList;
    }

    public Set<Integer> getFilmLikes(int filmId) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("SELECT USER_ID  FROM  FILM_LIKES"
                + " fl WHERE FILM_ID  =  ? ", filmId);
        Set<Integer> likes = new HashSet<>();
        while (filmRows.next()) {
            likes.add(filmRows.getInt("user_id"));
        }
        return likes;
    }

    @Override
    public Integer addLike(Integer filmId, Integer userId) {
        String sql = "INSERT INTO FILM_LIKES (FILM_ID, USER_ID) VALUES(?,?)";
        jdbcTemplate.update(sql, filmId, userId);
        return filmId;
    }

    @Override
    public Integer removeLike(Integer filmId, Integer userId) {
        String sql = "DELETE FROM FILM_LIKES WHERE (FILM_ID = ? AND USER_ID = ?)";
        jdbcTemplate.update(sql, filmId, userId);
        return filmId;
    }

}
