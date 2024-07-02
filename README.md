# INDE_GRP5
## Contexte:

Le gouvernement d’un pays qui prépare les Jeux Olympiques dans sa capitale décide d’une mesure pour limiter la fréquentation des rues de celle-ci.

La capitale étant très dense, il n’est pas possible que les citoyens puissent fréquenter normalement la ville en plus des touristes.

Le gouvernement décide alors de procéder à une campagne de vaccination visant à injecter une puce connectée.

Cette puce envoie des données régulièrement sur sa position.

Lorsque que l’individu entre dans une zone interdite, la puce se met à vibrer et envoie des données sur le fait que l’individu n’a pas respecté la loi.

Le gouvernement peut ajouter de nouvelles zones interdites à tout moment en fonction de la situation.

Cette mesure prise par l'État vise bien sûr à “récompenser” les citoyens les plus obéissants. Par exemple il peut exiger à la fin du mois, un rapport avec les personnes qui ont été le plus assidues, et celles qui ont été les plus dissidentes, afin de les punir.

  

## Outils utilisés et implémentation:

Toutes les puces vont émettre à une certaine fréquence des données de :

-   User_id
    
-   Nom
    
-   Prénom
    
-   Métier
    
-   email
    
-   Timestamp
    
-   Position (nom de rue actuel)
    
-   Âge
    

  

Ces données vont être gérées par une <u>stream</u> Kafka.

Celle-ci aura cette forme :

<u>Producer</u>:

-   Envoi des informations (id, âge, coordonnées,  etc) des personnes en temps réel (puce vaccin)

<u>Consumer</u>:

-   Système de création de statistique / rapports
-   Système de vérification de localisation et envoie de message en cas d'alerte  / urgence

  

<u>Spark Consumers in Scala :</u>

Le premier consommateur Spark (Batch Processing) servira à réaliser les statistiques sur les déplacement de la population.

Il va lire les messages dans la stream Kafka et écrire ces messages traités dans un datalake.

Le second quant à lui (Streaming Processing) permettra la gestion des emplacements interdits et d’envoyer une alerte de la puce si l’utilisateur ne respecte pas les emplacements interdits.

<u>Storage :</u>


Les données traitées seront ensuite stockées dans différentes bases de données.

-   Datalake (en local) pour stocker les information essentielles aux rapports demandés par le gouvernement
-   Les rapports nécessitent une gestion fiable sans besoin d’instantanéité.
-   Le contenu du datalake sera donc traité pour créer des rapports une fois unique de manière distribuée en utilisant un dernier composant Spark.
-   Celui-ci permettra d'avoir des données parfaitement traitée pour répondre à plusieurs questions simples (Exemple : quels sont les corps de métiers les plus dissidents ?) et pour la génération de rapports.
-   MySQL (PostgreSQL) pour stocker les utilisateurs qui sont dans un endroit interdit afin de les envoyer une alerte (message qui sera envoyé via discord)
