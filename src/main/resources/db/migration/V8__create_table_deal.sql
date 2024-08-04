CREATE TABLE deal
(
    open_date TIMESTAMP WITH TIME ZONE NOT NULL PRIMARY KEY,
    close_date TIMESTAMP WITH TIME ZONE,
    vol double precision NOT NULL,
    "open" double precision NOT NULL,
    "close" double precision,
    tp double precision,
    sl double precision,
    side VARCHAR(10) ,
    symbol VARCHAR(10),
    strategy VARCHAR(50),
    stepper VARCHAR(10),
    status VARCHAR(10) NOT NULL,
    result double precision
);
