CREATE TABLE IF NOT EXISTS agencies(
    id SERIAL,
    number VARCHAR(5) NOT NULL,
    address VARCHAR(50) NOT NULL,
    accounts INT NOT NULL,
    PRIMARY KEY (id),
    UNIQUE(number)
);
