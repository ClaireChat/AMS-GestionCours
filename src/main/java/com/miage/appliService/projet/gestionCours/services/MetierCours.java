package com.miage.appliService.projet.gestionCours.services;

import com.miage.appliService.projet.gestionCours.entities.Cours;
import com.miage.appliService.projet.gestionCours.repo.CoursRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MetierCours {

    @Autowired
    private CoursRepository coursRepository;

    public List<Cours> getAllCours() {
        return coursRepository.getAllCours();
    }

    public Cours getCoursByNom(String nomCours) {
        return coursRepository.getCoursByNom(nomCours);
    }

    public List<String> listeLieux() {

    }
    public String choisirLieu() {

    }



    public String planifierCours() {

    }

    public String inscriptionCours(String nomCours, Membre membre) {
        Cours cours = coursRepository.getCoursByNom(nomCours);
        if(cours.getNbPlacesOccupees()==0 || cours.getNbPlacesOccupees()==1) {
           if(cours.ge)
            if (membre.niveau == cours.niveauCible) {

            }
        }
    }
}
