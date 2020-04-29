package com.miage.appliService.projet.gestionCours.rest;

import com.miage.appliService.projet.gestionCours.entities.Cours;
import com.miage.appliService.projet.gestionCours.repo.CoursRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cours")
public class CoursController {

    @Autowired
    private CoursRepository coursRepository;

    @PostMapping("/")
    Cours postCours(@RequestBody Cours cours) {
        return this.coursRepository.save(cours);
    }

    @GetMapping("/{id}")
    Cours getCours(@PathVariable("id") Cours c) {
        return c;
    }
}
