package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDbStorageTest {
    private final JdbcTemplate jdbcTemplate;

    @Test
    public void testCreateFilm() throws ValidationException, InvocationTargetException {
        Film newFilm = Film.builder()
                .name("asfsgd")
                .description("asfdfff")
                .releaseDate(LocalDate.of(1990, 1, 1))
                .duration(11)
                .build();
        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate);

        assertThat(filmDbStorage.findAll().size())
                .isEqualTo(0);

        filmDbStorage.create(newFilm);

        assertThat(filmDbStorage.findAll().size())
                .isEqualTo(1);
    }

    @Test
    public void testUpdateFilm() throws ValidationException, InvocationTargetException {
        Film newFilm = Film.builder()
                .name("asfsgd")
                .description("asfdfff")
                .releaseDate(LocalDate.of(1990, 1, 1))
                .duration(11)
                .build();

        Film newFilm2 = Film.builder()
                .name("asfd33gd")
                .description("asfd33fff")
                .releaseDate(LocalDate.of(1995, 1, 1))
                .duration(12)
                .build();

        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate);
        newFilm = filmDbStorage.create(newFilm);
        newFilm2 = filmDbStorage.update(newFilm);

        assertThat(newFilm2)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(newFilm);
           }

    @Test
    public void testGetGenres() {
        GenreDbStorage genreDbStorage = new GenreDbStorage(jdbcTemplate);

        List<Genre> genreList = genreDbStorage.getGenres();

        assertThat(genreList.size())
                .isEqualTo(6);

        assertThat(genreDbStorage.getGenreById(1))
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(Genre.builder().id(1).name("Комедия").build());
    }

    @Test
    public void testGetMpa() {
        MpaDbStorage mpaDbStorage = new MpaDbStorage(jdbcTemplate);
        List<Mpa> mpaList = mpaDbStorage.getMpa();

        assertThat(mpaList.size())
                .isEqualTo(5);

        assertThat(mpaDbStorage.getMpaById(1))
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(Mpa.builder().id(1).name("G"));
    }

    @Test
    public void testAddAndRemoveLike() throws ValidationException, InvocationTargetException {
        Film newFilm = Film.builder()
                .name("asfsgd")
                .description("asfdfff")
                .releaseDate(LocalDate.of(1990, 1, 1))
                .duration(11)
                .build();
        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate);

        newFilm =  filmDbStorage.create(newFilm);

        User newUser = User.builder()
                .email("user@email.ru")
                .login("vanya123")
                .name("Ivan")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        newUser = userStorage.create(newUser);

        assertThat(newFilm.getLikes().size())
                .isEqualTo(0);

        filmDbStorage.addLike(newFilm.getId(),newUser.getId());

        newFilm = filmDbStorage.getFilm(newFilm.getId());

        assertThat(newFilm.getLikes().size())
                .isEqualTo(1);

        filmDbStorage.removeLike(newFilm.getId(),newUser.getId());

        newFilm = filmDbStorage.getFilm(newFilm.getId());

        assertThat(newFilm.getLikes().size())
                .isEqualTo(0);
    }

}
