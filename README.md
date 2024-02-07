# Flink + Kafka

### about

- generate-data service creates 100k rows in postgres
- kafka-producer service reads the data and sends it to kafka
- flink-processor runs at the same time with a tumbling window of 5 mins and processes the sum of the costs based on customer_name's
- if a tumbling window has a window interval of 10 seconds, every incoming event from a data stream for 10 seconds will fall into the same window
- then we compare the results from flink by running a sql query and it's accurate

![screenshot](/screenshot/screenshot.png "screenshot").

### setup

- create jars in flink-processor folder

```
mvn clean install
```

- to start containers

```
docker-compose up -d
```

- kafka tricks:

```sh
kafka-topics.sh --bootstrap-server kafka:9092 --create --topic datastream
```

```sh
# to check list of topics
kafka-topics.sh --bootstrap-server kafka:9092 --list
```

- in code, for local testing, replace all the container name in url's with localhost. To make it work in docker, we need to make sure we have container names instead
