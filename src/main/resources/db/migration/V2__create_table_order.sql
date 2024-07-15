CREATE TABLE orderr
(
    order_link_id uuid        NOT NULL,
    symbol        varchar(30) NULL,
    side          varchar(30) NULL,
    "type"        varchar(30) NULL,
    status        varchar(30) NULL,
    qty           varchar(20) NULL,
    price         varchar(20) NULL,
    create_date   timestamp   NOT NULL,
    update_date   timestamp   NULL,
    CONSTRAINT order_pkey PRIMARY KEY (order_link_id)
);