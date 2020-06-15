# Description du projet

Gestion des cours de la plateforme « les MIAGistes sous l’eau »
Description de l'API REST : https://app.swaggerhub.com/apis/ClaireChat/GestionCours/1.0.0#/


# Base de données MongoDB

Commande docker :
docker run --name monmongo -p 27017:27017 -e MONGO_INITDB_ROOT_USERNAME=root -e MONGO_INITDB_ROOT_PASSWORD=root -e MONGO_INITDB_DATABASE=base mongo:latest