CREATE TABLE bar
(
    create_date TIMESTAMP PRIMARY KEY,
    symbol      VARCHAR(10) NULL,
    vol_buy     VARCHAR(10) NULL,
    vol_sell    VARCHAR(10) NULL,
    "open"      VARCHAR(10) NULL,
    "close"     VARCHAR(20) NULL,
    low         VARCHAR(20) NULL,
    high        VARCHAR(20) NULL
);
