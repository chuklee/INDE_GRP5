From : https://kafka.apache.org/quickstart
1er terminal Lancer Zookeeper :
`bin/zookeeper-server-start.sh config/zookeeper.properties`

2 ème terminal Lancer Kafka broker service
`bin/kafka-server-start.sh config/server.properties`

Créer un topic
`bin/kafka-topics.sh --create --topic my_topic --bootstrap-server localhost:9092`

Chopper des infos sur le topic (voir si il a bien été créé)
`bin/kafka-topics.sh --describe --topic my_topic --bootstrap-server localhost:9092`

Ecrire dans un topic
`bin/kafka-console-producer.sh --topic my_topic --bootstrap-server localhost:9092`

Envoyer csv 1000 samples (sur l'ordi de possu)
`bin/kafka-console-producer.sh --topic my_topic --bootstrap-server localhost:9092 < /mnt/c/Users/user_/Desktop/scala_data_eng/INDE_GRP5/data-eng/src/main/scala/puces0.csv`
