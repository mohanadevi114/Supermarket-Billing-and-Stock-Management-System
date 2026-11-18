CREATE DATABASE IF NOT EXISTS supermarket_db;
USE supermarket_db;

CREATE TABLE products (
    barcode VARCHAR(50) PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    stock INT NOT NULL DEFAULT 0
);

CREATE TABLE sales (
    bill_no VARCHAR(40) PRIMARY KEY,
    sale_date DATETIME NOT NULL,
    total_amount DECIMAL(12,2) NOT NULL
);

CREATE TABLE sale_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    bill_no VARCHAR(40) NOT NULL,
    barcode VARCHAR(50) NOT NULL,
    product_name VARCHAR(120) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    quantity INT NOT NULL,
    subtotal DECIMAL(12,2) NOT NULL,
    FOREIGN KEY (bill_no) REFERENCES sales(bill_no)
);

INSERT INTO products VALUES
('1001', 'Rice 1kg', 65.00, 50),
('1002', 'Milk 500ml', 30.00, 40),
('1003', 'Bread', 40.00, 25),
('1004', 'Soap', 35.00, 60),
('1005', 'Biscuits', 20.00, 100);

USE supermarket_db;
SELECT * FROM products;

USE supermarket_db;

SELECT barcode, name, price, stock
FROM products;

USE supermarket_db;

SELECT * FROM sales ORDER BY sale_date DESC;
SELECT * FROM sale_items ORDER BY id DESC;
SELECT * FROM products WHERE barcode = '1001';