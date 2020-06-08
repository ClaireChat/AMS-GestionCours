package com.miage.appliService.projet.gestionCours.app.entities;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class Seance {

    @JsonFormat
            (shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm")
    public LocalDateTime debutSeance;

    public Long idEnseignant;


    public Long getIdEnseignant() {
        return idEnseignant;
    }

    public void setIdEnseignant(Long idEnseignant) {
        this.idEnseignant = idEnseignant;
    }

    public LocalDateTime getDebutSeance() {
        return debutSeance;
    }

    public void setDebutSeance(LocalDateTime debutSeance) {
        this.debutSeance = debutSeance;
    }

    @Override
    public String toString() {
        return "Seance{" +
                "debutSeance=" + debutSeance +
                ", idEnseignant=" + idEnseignant +
                '}';
    }
}
