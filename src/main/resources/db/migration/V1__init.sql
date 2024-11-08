CREATE TABLE customer (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    address VARCHAR(100) NOT NULL
);

CREATE TABLE account (
    account_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_id BIGINT NOT NULL,
    iban VARCHAR(34) UNIQUE NOT NULL,
    balance DECIMAL(19, 2) DEFAULT 0.0,
    version BIGINT,
    FOREIGN KEY (customer_id) REFERENCES customer(id) ON DELETE CASCADE
);

INSERT INTO customer (first_name, email, address) VALUES ('John', 'john.doe@example.com', '123 Main St');
INSERT INTO customer (first_name, email, address) VALUES ('Jane', 'jane.smith@example.com', '456 Elm St');

INSERT INTO account (customer_id, iban, balance, version) VALUES (1, 'NL49RABO0417164300', 1000.00, 1);
INSERT INTO account (customer_id, iban, balance, version) VALUES (2, 'NL49RABO0417164301', 2000.00, 1);