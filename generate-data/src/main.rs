use rand::Rng;
use sqlx::postgres::PgPoolOptions;

#[tokio::main]
async fn main() {
    let pool = PgPoolOptions::new()
        .max_connections(5)
        .connect("postgres://postgres:postgres@postgres:5432/postgres")
        .await
        .expect("connection to db failed");

    let mut rng = rand::thread_rng();

    for i in 0..100_000 {
        let res = sqlx::query(
            "INSERT INTO orders (customer_id, customer_name, cost, item_name)
            VALUES ( $1, $2, $3, $4 );
            ",
        )
        .bind(i)
        .bind(format!("Customer {}", rng.gen_range(0..10)))
        .bind(10.5)
        .bind(format!("Item {}", rng.gen::<u32>()))
        .execute(&pool)
        .await;
        match res {
            Ok(_) => println!("inserted row number: {}", i),
            Err(e) => println!("failed to insert row: {}", e),
        }
    }
}
