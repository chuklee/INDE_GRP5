# INDE_GRP5
## Contexte:

Le gouvernement d’un pays qui prépare les Jeux Olympiques dans sa capitale décide d’une mesure pour limiter la fréquentation des rues de celle-ci.

La capitale étant très dense, il n’est pas possible que les citoyens puissent fréquenter normalement la ville en plus des touristes.

Le gouvernement décide alors de procéder à une campagne de vaccination visant à injecter une puce connectée.

Cette puce envoie des données régulièrement sur sa position.

Lorsque que l’individu entre dans une zone interdite, la puce se met à vibrer et envoie des données sur le fait que l’individu n’a pas respecté la loi.

Le gouvernement peut ajouter de nouvelles zones interdites à tout moment en fonction de la situation.

Cette mesure prise par l'État vise bien sûr à “récompenser” les citoyens les plus obéissants.

  

## Outils utilisés et implémentation:

  

Afin d’activer le compte utilisateur lors de l’injection, celle-ci va envoyer au démarrage un user_id configuré au préalable avant l’injection afin de signaler au système que celle-ci est maintenant active.

  

Toutes les puces vont émettre à une certaine fréquence des données de :

-   Timestamp
    
-   Position (nom de rue actuel)
    
-   User_id
    

  

La stream Kafka aura cette forme :

  

Producer:

-   Envoi des coordonnées des personnes en temps réel (puce vaccin)
    
-   Système d’inscription de la puce dans le système (puce vaccin)
    
-   Ajout de rue interdite (État)
    

Consumer:

-   Système de création de statistique
    
-   Système de vérification de localisation
    
-   Demande
    

  
  

Spark Consumers in Scala :

Le premier consommateur Spark servira à réaliser les statistiques sur les déplacement de la population

Le second quant à lui permettra la gestion des emplacements interdits et d’envoyer une alerte de la puce si l’utilisateur ne respecte pas les emplacements interdits.

  
  

Les données traitées seront ensuite stockées dans différentes bases de données.

-   Cassandra car besoin de temps réel sur les emplacements interdits
    
-   MySQL pour les rapports demandés par le gouvernement nécessitant donc une gestion fiable sans besoin d’instantanéité
    
-   Redis Databases clés–valeurs des puces associés à leur utilisateurs
