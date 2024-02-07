FROM rust:latest

COPY . .

RUN cargo build

RUN ["chmod", "+x", "./wait_for_it.sh"]

CMD ./wait_for_it.sh -s -t 30 $ZOOKEEPER_SERVER -- ./wait_for_it.sh -s -t 30 $KAFKA_SERVER -- cargo run

