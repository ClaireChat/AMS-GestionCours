package com.miage.appliService.projet.gestionCours.app.rest;

import com.miage.appliService.projet.gestionCours.app.entities.Cours;
import com.miage.appliService.projet.gestionCours.app.repo.CoursRepository;
import com.miage.appliService.projet.gestionCours.app.services.MetierCours;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cours")
public class CoursController {

    @Autowired
    private CoursRepository coursRepository;

    @Autowired
    private MetierCours metier;

    @RequestMapping("/")
    public String home() {
        return "Hello World!";
    }

    @RequestMapping("/all")
    public List<Cours> all() {
        return this.coursRepository.findAll();
    }

    @PostMapping("/add")
    public Cours add(@RequestBody Cours cours) {
        return this.coursRepository.save(cours);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable("id") String id) {
        this.coursRepository.deleteById(id);
    }

    @GetMapping("/getOne/{id}")
    public Cours getOne(@PathVariable String id) {
        Optional<Cours> cours = this.coursRepository.findById(id);
        return cours.get();
    }

    @PutMapping("/update/{id}")
    public String update(@PathVariable String id, @RequestBody Cours cours) {
        Optional<Cours> coursOptional = this.coursRepository.findById(id);

        if (!coursOptional.isPresent())
            return "Aucun cours ne correspond à cet identifiant";

        cours.setId(id);
        this.coursRepository.save(cours);

        return cours.toString();
    }

    /*@RequestMapping("/test")
    public List<String> test() {
        return this.metier.listeLieux();
    }*/

}
