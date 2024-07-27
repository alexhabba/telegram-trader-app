CREATE TABLE fractal
(
    create_date TIMESTAMP PRIMARY KEY,
    symbol      VARCHAR(10) NULL,
    high        VARCHAR(20) NULL,
    low         VARCHAR(20) NULL,
    "interval"  INT         NULL,
    count_bar   INT         NULL
);
