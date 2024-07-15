CREATE TABLE tick
(
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    symbol      VARCHAR(10)                 NULL,
    side        VARCHAR(10)                 NULL,
    quantity    VARCHAR(20)                 NULL,
    price       VARCHAR(20)                 NULL,
    instrument  VARCHAR(20)                 NULL,
    exchange    VARCHAR(20)                 NULL,
    last_tick   VARCHAR(20)                 NULL,
    create_date TIMESTAMP WITHOUT TIME ZONE NOT NULL
);
