package com.miage.appliService.projet.gestionCours.app.rest;

import com.miage.appliService.projet.gestionCours.app.entities.Cours;
import com.miage.appliService.projet.gestionCours.app.entities.Lieu;
import com.miage.appliService.projet.gestionCours.app.entities.Seance;
import com.miage.appliService.projet.gestionCours.app.repo.CoursRepository;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;

@RestController
@RequestMapping("/cours")
public class CoursController {

    private static final Logger log = LoggerFactory.getLogger(CoursController.class);
    private static String urlLieux = "https://data.toulouse-metropole.fr/api/records/1.0/search/?dataset=piscines";

    @Autowired
    private CoursRepository coursRepository;

    @RequestMapping("/")
    public String home() {
        return "Gestion des cours";
    }

    @GetMapping("/getAllCours")
    public Iterable<Cours> getAllCours() {
        log.info("/getAllCours requested");
        return this.coursRepository.findAll();
    }

    @GetMapping("/getCoursById/{id}")
    public Cours getCoursById(@PathVariable String id) {
        log.info("getCoursById (id:"+id+")");
        return this.coursRepository.findCoursById(id);
    }

    @GetMapping("/getCoursByNom/{nomCours}")
    public Cours getCoursByNom(@PathVariable String nomCours) {
        return this.coursRepository.findCoursByNom(nomCours);
    }

    @PostMapping("/create")
    public Cours create(@RequestBody Cours cours) throws Exception {
        //on vérifie que idLieu existe bien dans la liste des lieux existants
        if(cours.getIdLieu()!=null) {
            if(!verifIdLieuExiste(cours.getIdLieu()))
                throw new Exception("Erreur : aucun lieu ne correspond a cet identifiant");
        }
        return this.coursRepository.save(cours);
    }

    @PostMapping("/update/{id}")
    public Cours update(@RequestBody Cours newCours, @PathVariable String id) throws Exception {
        // on vérifie que idLieu existe bien dans la liste des lieux existants
        if(newCours.getIdLieu()!=null) {
            if(!verifIdLieuExiste(newCours.getIdLieu()))
                throw new Exception("Erreur : aucun lieu ne correspond a cet identifiant");
        }

        return this.coursRepository.findById(id).map(cours -> {
            cours.setNom(newCours.getNom());
            cours.setNiveauCible(newCours.getNiveauCible());
            cours.setDuree(newCours.getDuree());
            cours.setIdLieu(newCours.getIdLieu());
            cours.setNbPlacesOccupees(newCours.getNbPlacesOccupees());
            return this.coursRepository.save(cours);
        })
                .orElseGet(() -> {
                    newCours.setId(id);
                    return this.coursRepository.save(newCours);
                });
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable("id") String id) {
        this.coursRepository.deleteById(id);
    }

    @DeleteMapping("/deleteAll")
    public void deleteAll() {
        this.coursRepository.deleteAll();
    }

    @PostMapping("/inscrireCours/{idCours}/{idMembre}")
    public Cours inscrireCours(@PathVariable("idCours") String idCours, @PathVariable("idMembre") Long idMembre) throws Exception {
        Cours cours = this.coursRepository.findCoursById(idCours);

        //le membre ne doit pas être déjà inscrit au cours
        if(!cours.getListeMembres().contains(idMembre)) {
            //  le nombre de personnes maximales dans chaque cours est 2 personnes
            if(cours.getNbPlacesOccupees()>=0 && cours.getNbPlacesOccupees()<=1) {
                cours.addParticipant(idMembre);
                cours.setNbPlacesOccupees(cours.getListeMembres().size());
            }
            else
                throw new Exception("Erreur : nombre de places insuffisant");
        }
        else
            throw new Exception("Erreur : déjà inscrit au cours");
        return this.coursRepository.save(cours);
    }

    @PostMapping("/desinscrireCours/{idCours}/{idMembre}")
    public Cours desinscrireCours(@PathVariable("idCours") String idCours, @PathVariable("idMembre") Long idMembre) {
        Cours cours = this.coursRepository.findCoursById(idCours);
        ArrayList<Long> listeMembres = cours.getListeMembres();
        listeMembres.remove(idMembre);
        cours.setListeMembres(listeMembres);
        cours.setNbPlacesOccupees(cours.getListeMembres().size());
        return this.coursRepository.save(cours);
    }

    @GetMapping("/getLieux")
    public ArrayList<Lieu> getLieux() throws IOException {
        return getListeLieux();
    }

    @GetMapping("/getLieuById/{idLieu}")
    public Lieu getLieuById(@PathVariable String idLieu) throws Exception {
        ArrayList<Lieu> listeLieux = getListeLieux();
        Lieu res = null;
        int i = 0;
        while(res==null & i<listeLieux.size()) {
            Lieu lieu  = listeLieux.get(i);
            if(lieu.getId().equals(idLieu)) {
                res = lieu;
            }
            i++;
        }

        if(res==null)
            throw new Exception("Erreur : aucun lieu ne correspond a cet identifiant");

        return res;
    }

    /**
     * Permet d'ajouter une séance à un cours
     * @param seance
     * @param idCours
     * @return Cours
     * @throws Exception
     */
    @PostMapping("/addSeance/{idCours}")
    public Cours addSeance(@RequestBody Seance seance, @PathVariable String idCours) throws Exception {
        Cours cours = this.coursRepository.findCoursById(idCours);
        // on vérifie que l'enseignant est libre pendant les horaires de la séance
        if(verifDate(seance.getDebutSeance())) {
            if (verifEnseignantLibre(seance.getIdEnseignant(), seance, cours.getDuree())) {
                cours.addSeance(seance);
            } else
                throw new Exception("Erreur : L'enseignant est deja pris pendant ces horaires");
        }
        else
            throw new Exception("Erreur : le cours doit debuter dans au moins 7 jours");
        return this.coursRepository.save(cours);
    }

    /**
     * Permet de supprimer une séance d'un cours
     * @param idCours
     * @param idSeance
     * @return Cours
     * @throws Exception
     */
    @DeleteMapping("/deleteSeance/{idCours}/{idSeance}")
    public Cours deleteSeance(@PathVariable("idCours") String idCours, @PathVariable("idSeance") Integer idSeance) throws Exception {
        Cours cours = this.coursRepository.findCoursById(idCours);
        if(cours.getListeSeances().containsKey(idSeance))
            cours.getListeSeances().remove(idSeance);
        else
            throw new Exception("Erreur : Id de seance inexistant");

        return this.coursRepository.save(cours);
    }

    /**
     * Permet de supprimer une séance d'un cours
     * @param seance
     * @param idCours
     * @param idSeance
     * @return Cours
     * @throws Exception
     */
    @PostMapping("/updateSeance/{idCours}/{idSeance}")
    public Cours updateSeance(@RequestBody Seance seance, @PathVariable String idCours, @PathVariable Integer idSeance) throws Exception {
        Cours cours = this.coursRepository.findCoursById(idCours);
        if(cours.getListeSeances().containsKey(idSeance)) {
            // on vérifie que l'enseignant est libre pendant les horaires de la séance
            if (verifEnseignantLibre(seance.getIdEnseignant(), seance, cours.getDuree())) {
                cours.getListeSeances().put(idSeance, seance);
            } else
                throw new Exception("Erreur : L'enseignant est deja pris pendant ces horaires");
        }
        else
            throw new Exception("Erreur : Id de seance inexistant");

        return this.coursRepository.save(cours);
    }

    /**
     * Vérifie que la date passée en paramètre est supérieure de 7 jours par rapport à la date actuelle
     * @param date
     * @return boolean
     */
    private boolean verifDate(LocalDateTime date) {
        Calendar c = Calendar.getInstance();
        Date currentDate = new Date();
        c.setTime(currentDate);
        c.add(Calendar.DAY_OF_MONTH, 7);

        return c.getTime().before(Date
                .from(date.atZone(ZoneId.systemDefault())
                        .toInstant()));
    }

    /**
     * Renvoie la liste des lieux disponibles depuis l'API https://data.toulouse-metropole.fr/api/records/1.0/search/?dataset=piscines
     * @return ArrayList<Lieu>
     * @throws IOException
     */
    private ArrayList<Lieu> getListeLieux() throws IOException {
        URL url = new URL(urlLieux);
        String genreJson = IOUtils.toString(url);
        JSONObject objet = new JSONObject(genreJson);
        JSONArray jsonArray = (JSONArray) objet.get("records");
        ArrayList<Lieu> listeLieux = new ArrayList<>();

        // Parcours de la JSONArray pour créer l'ArrayList de lieux avec les informations qui nous intéressent
        for (int i = 0; i < jsonArray.length(); i++) {
            Lieu lieu = new Lieu();
            JSONObject row = jsonArray.getJSONObject(i);
            lieu.setId(row.getString("recordid"));
            JSONObject fields = row.getJSONObject("fields");
            lieu.setSaison(fields.getString("saison"));
            lieu.setAdresse(fields.getString("adresse"));
            lieu.setIndex(fields.getString("index"));
            lieu.setNom(fields.getString("nom_complet"));
            lieu.setTelephone(fields.getString("telephone"));

            JSONArray jsonCoord = fields.getJSONObject("geo_shape").getJSONArray("coordinates");
            List<Double> listeCoord = new ArrayList<>();
            listeCoord.add((Double)jsonCoord.get(0));
            listeCoord.add((Double)jsonCoord.get(1));
            lieu.setCoordonnees(listeCoord);

            listeLieux.add(lieu);
        }
        return listeLieux;
    }

    /**
     * Vérifie que l'id passé en paramètre est présent dans la liste des lieux existants
     * @param idLieu
     * @return
     * @throws IOException
     */
    private boolean verifIdLieuExiste(String idLieu) throws IOException {
        int i = 0;
        boolean idLieuExiste = false;
        ArrayList<Lieu> listeLieux = getListeLieux();
        while (!idLieuExiste & i < listeLieux.size()) {
            Lieu lieu = listeLieux.get(i);
            if (lieu.getId().equals(idLieu))
                idLieuExiste = true;
            i++;
        }
        return idLieuExiste;
    }

    private boolean verifEnseignantLibre(Long idEnseignant, Seance newSeance, int duree) {
        boolean res = true;
        List<Cours> listeCours = this.coursRepository.findAll();
        // Parcours de tous les cours existants
        for(Cours cours : listeCours) {
            HashMap<Integer, Seance> mapSeances = cours.getListeSeances();
            // Parcours de séances du cours
            for(Seance seance : mapSeances.values()) {
                // on vérifie que c'est le même enseignant et le même jour
                if(idEnseignant.equals(seance.getIdEnseignant())) {
                    if (seance.getDebutSeance().toLocalDate().equals(newSeance.getDebutSeance().toLocalDate())) {
                        // on récupère les horaires
                        LocalTime debutSeance = seance.getDebutSeance().toLocalTime();
                        LocalTime finSeance = debutSeance.plusMinutes(cours.getDuree());
                        LocalTime debutNewSeance = newSeance.getDebutSeance().toLocalTime();
                        LocalTime finNewSeance = debutNewSeance.plusMinutes(duree);
                        //heureDebutSeance < heureDebutNewSeance < heureFinSeance
                        if (debutNewSeance.isAfter(debutSeance) && debutNewSeance.isBefore(finSeance))
                            return false;
                        //heureDebutSeance < heureFinNewSeance < heureFinSeance
                        if (finNewSeance.isAfter(debutSeance) && debutNewSeance.isBefore(finSeance))
                            return false;
                        //heureDebutNewSeance < heureDebutSeance && heureFinNewSeance > heureFinSeance
                        if (debutNewSeance.isBefore(debutSeance) && finNewSeance.isAfter(finSeance))
                            return false;
                    }
                }
            }
        }
        return true;
    }
}
