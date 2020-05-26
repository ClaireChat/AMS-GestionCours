package com.miage.appliService.projet.gestionCours.app.rest;

import com.miage.appliService.projet.gestionCours.app.entities.Cours;
import com.miage.appliService.projet.gestionCours.app.repo.CoursRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/cours")
public class CoursController {
    private static final Logger log = LoggerFactory.getLogger(CoursController.class);

    @Autowired
    private CoursRepository coursRepository;

    private boolean verifDate(Date date) {
        Calendar c = Calendar.getInstance();
        Date currentDate = new Date();
        c.setTime(currentDate);
        c.add(Calendar.DAY_OF_MONTH, 7);

        if(!c.getTime().before(date))
            return false;
        return true;
    }

    @RequestMapping("/")
    public String home() {
        return "Hello World!";
    }

    @GetMapping("/getAllCours")
    public Iterable<Cours> getAllCours() {
        log.info("/getAllCours requested");
        return this.coursRepository.findAll();
    }

    @GetMapping("/getCoursById/{id}")
    public Cours getCoursById(@PathVariable String id) {
        return this.coursRepository.findCoursById(id);
    }

    @GetMapping("/getCoursByNom/{nomCours}")
    public Cours getCoursByNom(@PathVariable String nomCours) {
        return this.coursRepository.findCoursByNom(nomCours);
    }

    @PostMapping("/create")
    public Cours create(@RequestBody Cours cours) throws Exception {
        // la date d’un cours doit être supérieure de 7 jours par rapport à la date de saisie
        if(!verifDate(cours.getCreneau()))
            throw new Exception("Erreur : le cours doit débuter dans au moins 7 jours");
        return this.coursRepository.save(cours);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable("id") String id) {
        this.coursRepository.deleteById(id);
    }


    @PutMapping("/update/{id}")
    public Cours update(@RequestBody Cours newCours, @PathVariable String id) throws Exception {
        // la date d’un cours doit être supérieure de 7 jours par rapport à la date de saisie
        if(!verifDate(newCours.getCreneau()))
            throw new Exception("Erreur : le cours doit débuter dans au moins 7 jours");

        return this.coursRepository.findById(id).map(cours -> {
            cours.setNom(newCours.getNom());
            cours.setNiveauCible(newCours.getNiveauCible());
            cours.setDuree(newCours.getDuree());
            cours.setIdEnseignant(newCours.getIdEnseignant());
            cours.setCreneau(newCours.getCreneau());
            cours.setNbPlacesOccupees(newCours.getNbPlacesOccupees());
            cours.setLieu(newCours.getLieu());
            return this.coursRepository.save(cours);
        })
        .orElseGet(() -> {
            newCours.setId(id);
            return this.coursRepository.save(newCours);
        });
    }

    @PutMapping("/inscrireCours/{idCours}/{idMembre}")
    public Cours inscrireCours(@PathVariable("idCours") String idCours, @PathVariable("idMembre") Long idMembre) throws Exception {
        Cours cours = this.coursRepository.findCoursById(idCours);

        //le membre ne doit pas être déjà inscrit au cours
        if(!cours.getListeMembres().contains(idMembre)) {
            //  le nombre de personnes maximales dans chaque cours est 2 personnes
            if(cours.getNbPlacesOccupees()>=0 && cours.getNbPlacesOccupees()<=1) {
                cours.setNbPlacesOccupees(cours.getNbPlacesOccupees() + 1);
                cours.addParticipant(idMembre);
            }
            else
                throw new Exception("Erreur : nombre de places insuffisant");
        }
        else
            throw new Exception("Erreur : déjà inscrit au cours");
        return this.coursRepository.save(cours);
    }

    @PutMapping("/desinscrireCours/{idCours}/{idMembre}")
    public Cours desinscrireCours(@PathVariable("idCours") String idCours, @PathVariable("idMembre") Long idMembre) {
        Cours cours = this.coursRepository.findCoursById(idCours);
        ArrayList<Long> listeMembres = cours.getListeMembres();
        listeMembres.remove(idMembre);
        cours.setListeMembres(listeMembres);
        return this.coursRepository.save(cours);
    }

    /*
    @RequestMapping("/getLieux")
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
