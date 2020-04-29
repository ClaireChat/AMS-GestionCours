package com.miage.appliService.projet.gestionCours.services;

import com.miage.appliService.projet.gestionCours.entities.Cours;
import com.miage.appliService.projet.gestionCours.entities.Membre;
import com.miage.appliService.projet.gestionCours.repo.CoursRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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

    public String planifierCours(Membre enseignant, String nomCours, int niveau, int duree, DateTimeFormatter creneau) {

        if(enseignant.type=="enseignant" && enseignant.niveau>=niveau) {
            // on vérifie que le créneau souhaité est libre
            for(Cours coursExistant : coursRepository.getAllCours()) {
                if(coursExistant.getCreneau()==creneau)
                    return "Planification impossible : créneau déjà pris";
            }
            Cours cours = new Cours();
            cours.setNomCours(nomCours);
            cours.setNiveauCible(niveau);
            cours.setCreneau(creneau);
            cours.setDuree(duree);
            cours.setEnseignant(enseignant.getId());
            return "Planification ajoutée";
        }
        return "Planification impossible : votre niveau doit être supérieur au niveau cible du cours";
    }

    public String inscriptionCours(String nomCours, Membre membre) {
        //Cours cours = coursRepository.getCoursByNom(nomCours);
        Cours cours = new Cours();

        //  le nombre de personnes maximales dans chaque cours est  2 personnes
        if(cours.getNbPlacesOccupees()>=0 && cours.getNbPlacesOccupees()<=1) {
            //VERIFIER DATE
            // la date d’un cours doit être supérieure de 7 jours par rapport à la date de saisie
            // le niveau d’expertise est égal à celui ciblé par le cours.
            if (membre.niveau == cours.niveauCible) {
                cours.setNbPlacesOccupees(cours.getNbPlacesOccupees()+1);
                cours.addParticipant(membre);
                return "Inscription effectuée";
            }
            return"Inscription impossible : le niveau " + cours.niveauCible + " est requis";
        }
        return "Inscription impossible : nombre de places insuffisant";
    }

    public List<String> listeLieux()
    {
        List<String> listeLieux=new ArrayList<String>();

        final String uri = "https://data.toulouse-metropole.fr/explore/embed/dataset/piscines";
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(uri, String.class);

        System.out.println(result);

        return listeLieux;
    }

    public String choisirLieu() {
        return null;
    }
}
