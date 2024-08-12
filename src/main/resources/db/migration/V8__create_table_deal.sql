CREATE TABLE deal
(
    id         uuid             NOT NULL,
    open_date  TIMESTAMP,
    close_date TIMESTAMP,
    vol        double precision NOT NULL,
    "open"     double precision NOT NULL,
    "close"    double precision,
    tp         double precision,
    sl         double precision,
    side       VARCHAR(10),
    symbol     VARCHAR(10),
    strategy   VARCHAR(50),
    stepper    VARCHAR(10),
    status     VARCHAR(10)      NOT NULL,
    result     double precision,
    CONSTRAINT deal_pkey PRIMARY KEY (id)
);
