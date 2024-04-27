package ru.yandex.practicum.filmorate.controller;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@Getter
@Controller
@Slf4j
@RestController
@RequestMapping
public class MpaController {

    MpaService mpaService;

    public MpaController(MpaService mpaService) {
        this.mpaService = mpaService;
    }

    @GetMapping("/mpa")
    public List<Mpa> getMpa() {
        return mpaService.getMpa();
    }

    @GetMapping("/mpa/{id}")
    public Mpa getMpa(@PathVariable Integer id) {
        return mpaService.getMpaById(id);
    }
}
