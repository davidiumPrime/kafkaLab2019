# kafkaLab2019
Kafka Experiments 2019

Console Command Cheatsheet
=====



bin/zookeeper-server-start.sh config/zookeeper.properties 

======


bin/kafka-server-start.sh config/server.properties 


======

bin/kafka-console-consumer.sh --bootstrap-server 127.0.0.1:9092 --topic first_topic --from-beginning
bin/kafka-console-producer.sh --broker-list 127.0.0.1:9092 --topic first_topic 

======

bin/kafka-topics.sh --describe --zookeeper 127.0.0.1:9092
