package com.miage.appliService.projet.gestionCours.entities;

import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

/**
 *
 * @author clairechatenet
 */
@Document(collection = "Cours")
public class Cours {

    @Id
    @GeneratedValue
    public long id;

    @NotNull
    public String nomCours;

    @NotNull
    public int niveauCible;

    @NotNull
    public String idEnseignant;

    @ManyToOne
    public Long enseignant;

    @NotNull
    public String lieu;

    @NotNull
    public String creneau;

    @NotNull
    public int duree;

    public int nbPlacesOccupees;

    public Cours() {
    }

    public Cours(@NotNull String nomCours, @NotNull int niveauCible, @NotNull String idEnseignant, Long enseignant, @NotNull String lieu, @NotNull String creneau, @NotNull int duree) {
        this.nomCours = nomCours;
        this.niveauCible = niveauCible;
        this.idEnseignant = idEnseignant;
        this.enseignant = enseignant;
        this.lieu = lieu;
        this.creneau = creneau;
        this.duree = duree;
        this.nbPlacesOccupees=0;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getNiveauCible() {
        return niveauCible;
    }

    public void setNiveauCible(int niveauCible) {
        this.niveauCible = niveauCible;
    }

    public String getIdEnseignant() {
        return idEnseignant;
    }

    public void setIdEnseignant(String idEnseignant) {
        this.idEnseignant = idEnseignant;
    }

    public Long getEnseignant() {
        return enseignant;
    }

    public void setEnseignant(Long enseignant) {
        this.enseignant = enseignant;
    }

    public String getLieu() {
        return lieu;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    public String getCreneau() {
        return creneau;
    }

    public void setCreneau(String creneau) {
        this.creneau = creneau;
    }

    public int getDuree() {
        return duree;
    }

    public void setDuree(int duree) {
        this.duree = duree;
    }

    public String getNomCours() {
        return nomCours;
    }

    public void setNomCours(String nomCours) {
        this.nomCours = nomCours;
    }

    public int getNbPlacesOccupees() {
        return nbPlacesOccupees;
    }

    public void setNbPlacesOccupees(int nbPlacesOccupees) {
        this.nbPlacesOccupees = nbPlacesOccupees;
    }
}
