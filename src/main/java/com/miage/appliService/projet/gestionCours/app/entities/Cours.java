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
//@Entity
public class Cours {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public String id;

    @NotNull
    public String nom;

    @NotNull
    public int niveauCible;

    @NotNull
    public Long idEnseignant;

    @NotNull
    public String lieu;

    @NotNull
    @JsonFormat
            (shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm")
    public Date creneau;

    @NotNull
    //durée exprimée en minutes
    public int duree;

    public int nbPlacesOccupees;

    //liste des membres participant au cours
    public ArrayList<Membre> listeMembres;

    public Cours() {
    }


    public Cours(@NotNull String nom, @NotNull int niveauCible, @NotNull Long idEnseignant, @NotNull String lieu, @NotNull Date creneau, @NotNull int duree) {
        this.nom = nom;
        this.niveauCible = niveauCible;
        this.idEnseignant = idEnseignant;
        this.lieu = lieu;
        this.creneau = creneau;
        this.duree = duree;
        this.nbPlacesOccupees=0;
        this.listeMembres=new ArrayList<Membre>();
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

    public String getLieu() {
        return lieu;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
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

    public void addParticipant(Membre membre) {
        this.listeMembres.add(membre);
    }

    @Override
    public String toString() {
        return "Cours{" +
                "id='" + id + '\'' +
                ", nom='" + nom + '\'' +
                ", niveauCible=" + niveauCible +
                ", idEnseignant=" + idEnseignant +
                ", lieu='" + lieu + '\'' +
                ", creneau=" + creneau +
                ", duree=" + duree +
                ", nbPlacesOccupees=" + nbPlacesOccupees +
                ", listeMembres=" + listeMembres +
                '}';
    }
}
