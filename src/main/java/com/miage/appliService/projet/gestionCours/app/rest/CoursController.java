package com.miage.appliService.projet.gestionCours.app.rest;

import com.miage.appliService.projet.gestionCours.app.entities.Cours;
import com.miage.appliService.projet.gestionCours.app.entities.Seance;
import com.miage.appliService.projet.gestionCours.app.repo.CoursRepository;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;

@RestController
@RequestMapping("/cours")
public class CoursController {
    private static final Logger log = LoggerFactory.getLogger(CoursController.class);

    @Autowired
    private CoursRepository coursRepository;


    private static String urlLieux = "https://data.toulouse-metropole.fr/api/records/1.0/search/?dataset=piscines";


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

    @PutMapping("/update/{id}")
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
            cours.setNbPlacesOccupees(newCours.getNbPlacesOccupees());
            cours.setIdLieu(newCours.getIdLieu());
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

    @GetMapping("/getLieux")
    public ArrayList<JSONObject> getLieux() throws IOException, ParseException {
        JSONArray jsonArrayLieux = getJSONArrayLieux();
        return jsonArrayLieux;
    }

    @GetMapping("/getLieuFromId/{idLieu}")
    public JSONObject getLieuFromId(@PathVariable String idLieu) throws Exception {
        JSONArray jsonArrayLieux = getJSONArrayLieux();
        JSONObject lieu = null;
        int i = 0;
        while(lieu==null & i<jsonArrayLieux.size()) {
            JSONObject jsonobj  = ((JSONObject)jsonArrayLieux.get(i));
            if(jsonobj.get("recordid").equals(idLieu)) {
                lieu = (JSONObject) jsonobj.get("fields");
            }
            i++;
        }

        if(lieu==null)
            throw new Exception("Erreur : aucun lieu ne correspond a cet identifiant");

        return lieu;
    }

    /**
     * Permet d'ajouter une séance à un cours
     * @param seance
     * @param idCours
     * @return Cours
     * @throws Exception
     */
    @PutMapping("/addSeance/{idCours}")
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
    @PutMapping("/updateSeance/{idCours}/{idSeance}")
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

        if(!c.getTime().before(java.util.Date
                .from(date.atZone(ZoneId.systemDefault())
                        .toInstant())))
            return false;
        return true;
    }

    /**
     * Renvoie la liste des lieux disponibles dans l'API https://data.toulouse-metropole.fr/api/records/1.0/search/?dataset=piscines
     * @return JSONArray
     * @throws IOException
     * @throws ParseException
     */
    private JSONArray getJSONArrayLieux() throws IOException, ParseException {
        URL url = new URL(urlLieux);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();
        int responsecode = conn.getResponseCode();
        JSONArray res = null;

        if(responsecode != 200)
            throw new RuntimeException("HttpResponseCode: " +responsecode);
        else {
            Scanner sc = new Scanner(url.openStream());
            String inline = "";
            while (sc.hasNext()) {
                inline += sc.nextLine();
            }
            sc.close();
            JSONParser parse = new JSONParser();
            JSONObject jobj = (JSONObject) parse.parse(inline);
            res = (JSONArray) jobj.get("records");
        }
        conn.disconnect();
        return res;
    }

    /**
     * Vérifie que l'id passé en paramètre est présent dans la liste des lieux existants
     * @param idLieu
     * @return
     * @throws IOException
     * @throws ParseException
     */
    private boolean verifIdLieuExiste(String idLieu) throws IOException, ParseException {
        int i = 0;
        boolean idLieuExiste = false;
        JSONArray jsonArrayLieux = getJSONArrayLieux();
        while (!idLieuExiste & i < jsonArrayLieux.size()) {
            JSONObject jsonobj = ((JSONObject) jsonArrayLieux.get(i));
            if (jsonobj.get("recordid").equals(idLieu))
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
