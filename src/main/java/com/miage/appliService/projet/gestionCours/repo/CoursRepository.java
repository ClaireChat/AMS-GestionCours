package com.miage.appliService.projet.gestionCours.repo;


import com.miage.appliService.projet.gestionCours.entities.Cours;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *
 * @author clairechatenet
 */
@Repository
public interface CoursRepository extends MongoRepository<Cours,String> {


    public List<Cours> getAllCours();

    @Query("{ 'nomCours' : ?0 }")
    public Cours getCoursByNom(String nomCours);

    public Cours save(Cours cours);
}
