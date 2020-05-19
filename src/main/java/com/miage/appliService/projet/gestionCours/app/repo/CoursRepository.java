package com.miage.appliService.projet.gestionCours.app.repo;


import com.miage.appliService.projet.gestionCours.app.entities.Cours;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


/**
 *
 * @author clairechatenet
 */
@Repository
public interface CoursRepository extends MongoRepository<Cours,String> {

    Cours findCoursByNom(String nom);
    Cours findCoursById(String id);

}
