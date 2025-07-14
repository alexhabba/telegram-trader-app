CREATE TABLE account (
    id UUID PRIMARY KEY,
    name VARCHAR(255),
    key VARCHAR(255),
    secret VARCHAR(255),
    create_date TIMESTAMP
);
