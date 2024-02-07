CREATE TABLE orders (
    id SERIAL PRIMARY KEY,
    customer_id INT,
    customer_name VARCHAR(255),
    cost DOUBLE PRECISION,
    item_name VARCHAR(255)
)
