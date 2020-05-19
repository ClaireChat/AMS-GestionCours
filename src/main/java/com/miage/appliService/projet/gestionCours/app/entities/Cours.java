package com.miage.appliService.projet.gestionCours.app.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author clairechatenet
 */
@Document(collection = "Cours")
public class Cours {

    @Id
    @GeneratedValue
    public String id;

    @NotNull(message = "nom not null")
    @Column(unique = true)
    public String nom;

    @NotNull
    public int niveauCible;

    @NotNull
    public Long idEnseignant;

    @NotNull
    public String idLieu;

    @NotNull
    @JsonFormat
            (shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm")
    public Date creneau;

    @NotNull
    //durée exprimée en minutes
    public int duree;

    public int nbPlacesOccupees;

    //liste des membres participant au cours
    public ArrayList<Long> listeMembres;

    public Cours() {
        this.idLieu = null;
        this.nbPlacesOccupees=0;
        this.listeMembres=new ArrayList<Long>();
    }


    public Cours(@NotNull String nom, @NotNull int niveauCible, @NotNull Long idEnseignant, @NotNull Date creneau, @NotNull int duree) {
        this.nom = nom;
        this.niveauCible = niveauCible;
        this.idEnseignant = idEnseignant;
        this.creneau = creneau;
        this.duree = duree;
        this.idLieu = null;
        this.nbPlacesOccupees=0;
        this.listeMembres=new ArrayList<Long>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getNiveauCible() {
        return niveauCible;
    }

    public void setNiveauCible(int niveauCible) {
        this.niveauCible = niveauCible;
    }

    public Long getIdEnseignant() {
        return idEnseignant;
    }

    public void setIdEnseignant(Long idEnseignant) {
        this.idEnseignant = idEnseignant;
    }

    public String getIdLieu() {
        return idLieu;
    }

    public void setIdLieu(String idLieu) {
        this.idLieu = idLieu;
    }

    public Date getCreneau() {
        return creneau;
    }

    public void setCreneau(Date creneau) {
        this.creneau = creneau;
    }

    public int getDuree() {
        return duree;
    }

    public void setDuree(int duree) {
        this.duree = duree;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getNbPlacesOccupees() {
        return nbPlacesOccupees;
    }

    public void setNbPlacesOccupees(int nbPlacesOccupees) {
        this.nbPlacesOccupees = nbPlacesOccupees;
    }

    public ArrayList<Long> getListeMembres() {
        return listeMembres;
    }

    public void setListeMembres(ArrayList<Long> listeMembres) {
        this.listeMembres = listeMembres;
    }

    public void addParticipant(Long idMembre) {
        this.listeMembres.add(idMembre);
    }

    @Override
    public String toString() {
        return "Cours{" +
                "id='" + id + '\'' +
                ", nom='" + nom + '\'' +
                ", niveauCible=" + niveauCible +
                ", idEnseignant=" + idEnseignant +
                ", lieu='" + idLieu + '\'' +
                ", creneau=" + creneau +
                ", duree=" + duree +
                ", nbPlacesOccupees=" + nbPlacesOccupees +
                ", listeMembres=" + listeMembres +
                '}';
    }
}
