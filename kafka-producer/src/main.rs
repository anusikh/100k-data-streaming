use std::time::Duration;

use kafka::producer::{Producer, Record};
use serde::{Deserialize, Serialize};
use sqlx::{
    postgres::{PgPoolOptions, PgRow},
    Row,
};

#[derive(Serialize, Deserialize)]
pub struct Data {
    customer_name: String,
    cost: f64,
}

#[derive(Serialize, Deserialize, Debug)]
pub struct DbData {
    id: i32,
    customer_id: i32,
    customer_name: String,
    cost: f64,
    item_name: String,
}

#[tokio::main]
async fn main() {
    let pool = PgPoolOptions::new()
        .max_connections(5)
        .connect("postgres://postgres:postgres@postgres:5432/postgres")
        .await
        .expect("connection to db failed");

    let mut producer = Producer::from_hosts(vec!["kafka:9092".to_owned()])
        .with_ack_timeout(Duration::from_secs(1))
        .with_required_acks(kafka::producer::RequiredAcks::One)
        .create()
        .unwrap();

    let res = sqlx::query("SELECT * FROM orders")
        .map(|row: PgRow| DbData {
            id: row.get("id"),
            customer_id: row.get("customer_id"),
            customer_name: row.get("customer_name"),
            cost: row.get("cost"),
            item_name: row.get("item_name"),
        })
        .fetch_all(&pool)
        .await;

    match res {
        Ok(datum) => {
            let mut count = 0;
            for data in datum {
                println!("{:?}", data);

                let d = Data {
                    customer_name: data.customer_name,
                    cost: data.cost,
                };

                let buffer = serde_json::to_string(&d).unwrap();
                let res = producer.send(&Record::from_value("datastream", buffer.as_bytes()));
                match res {
                    Ok(_) => {
                        count = count + 1;
                        println!("sent to kafka {}", &count);
                    }
                    Err(e) => println!("something went wrong: {}", e),
                }
            }
        }
        _ => {
            println!("couldn't fetch the orders")
        }
    }
}
