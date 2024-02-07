FROM openjdk:17

WORKDIR /
COPY target/flink-processor-1.0-SNAPSHOT-jar-with-dependencies.jar flink-processor.jar

CMD  java -Xmx512m -jar flink-processor.jar