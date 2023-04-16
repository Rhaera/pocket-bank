CREATE TABLE IF NOT EXISTS accounts(
    id SERIAL,
    number VARCHAR(10) NOT NULL,
    agency VARCHAR(5) NOT NULL,
    cpf VARCHAR(15) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE(number)
);
