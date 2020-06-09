package com.miage.appliService.projet.gestionCours.app.entities;

import java.util.List;

public class Lieu {

    String id;
    String nom;
    String saison;
    List<Double> coordonnees;
    String adresse;
    String telephone;
    String index;


    public Lieu(String nom, String saison, List<Double> coordonnees, String adresse, String telephone, String index) {
        this.nom = nom;
        this.saison = saison;
        this.coordonnees = coordonnees;
        this.adresse = adresse;
        this.telephone = telephone;
        this.index = index;
    }

    public Lieu() {
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getSaison() {
        return saison;
    }

    public void setSaison(String saison) {
        this.saison = saison;
    }

    public List<Double> getCoordonnees() {
        return coordonnees;
    }

    public void setCoordonnees(List<Double> coordonnees) {
        this.coordonnees = coordonnees;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
