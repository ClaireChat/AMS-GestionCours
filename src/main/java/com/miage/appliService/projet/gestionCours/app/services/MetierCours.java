package com.miage.appliService.projet.gestionCours.app.services;

import com.miage.appliService.projet.gestionCours.app.entities.Cours;
import com.miage.appliService.projet.gestionCours.app.entities.Membre;
import com.miage.appliService.projet.gestionCours.app.repo.CoursRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class MetierCours {

    @Autowired
    private CoursRepository coursRepository;

    public List<Cours> getAllCours() {
        return this.coursRepository.findAll();
    }

    public Cours getCoursByNom(String nomCours) {
        return this.coursRepository.findCoursByNom(nomCours);
    }

    public String planifierCours(Membre enseignant, String nom, int niveau, int duree, Date creneau) {

        if(enseignant.type=="enseignant" && enseignant.niveau>=niveau) {
            // on vérifie que le créneau souhaité est libre
            for(Cours coursExistant : this.coursRepository.findAll()) {
                if(coursExistant.getCreneau().equals(creneau))
                    return "Planification impossible : créneau déjà pris";
            }
            Cours cours = new Cours();
            cours.setNom(nom);
            cours.setNiveauCible(niveau);
            cours.setCreneau(creneau);
            cours.setDuree(duree);
            cours.setIdEnseignant(enseignant.getId());
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
                this.coursRepository.save(cours);
                return "Inscription effectuée";
            }
            return"Inscription impossible : le niveau " + cours.niveauCible + " est requis";
        }
        return "Inscription impossible : nombre de places insuffisant";
    }
    /*
    public List<String> listeLieux()
    {
        List<String> listeLieux=new ArrayList<String>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            List<Lieu> lieux = mapper.readValue(new URL("https://data.toulouse-metropole.fr/api/records/1.0/search/?dataset=piscines"),  new TypeReference<List<Lieu>>(){});
            //RestTemplate restTemplate = new RestTemplate();
            //Lieu result = restTemplate.getForObject(uri, Lieu.class);
            //listeLieux.add(result+"/n");
            //System.out.println(result);
             for(Lieu lieu : lieux) {
                 System.out.println(lieu);
             }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return listeLieux;
    }*/

    public String choisirLieu() {
        return null;
    }
}
