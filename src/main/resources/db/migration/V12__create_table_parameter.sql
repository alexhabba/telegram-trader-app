CREATE TABLE parameter
(
    id                  UUID PRIMARY KEY,
    symbol              VARCHAR(13),
    sl                  DOUBLE PRECISION,
    tp                  DOUBLE PRECISION,
    vol                 DOUBLE PRECISION,
    vol_position        DOUBLE PRECISION,
    minute              INT,
    shift               DOUBLE PRECISION,
    coefficient         DOUBLE PRECISION,
    strategy            INT,
    description         VARCHAR(2000)
);

INSERT INTO trader."parameter"
(id, symbol, sl, tp, vol, vol_position, "minute", shift, coefficient, strategy, description)
VALUES('f831116a-7381-4e27-a148-85300d97de35'::uuid, 'SOL', 1.1, 4.9, 1900.0, 0.4, 61, 0.7, 1.4, 7, 'commonResult : 103
result : 259.4505200000001
убыточных сделок : 154
успешных сделок : 69
maxVolInStrategy : 0.4
баланс стал таким : 133.78020800000003736');

INSERT INTO bot."parameter"
(id, symbol, sl, tp, vol, vol_position, "minute", shift, coefficient, strategy, description)
VALUES('49c08ab4-f01a-4b61-87d9-b356c5492ec6'::uuid, 'SOL', 1.3, 5.7, 3300.0, 0.4, 91, 0.5, 1.0, 8, 'commonResult : 95
result : 240.14960000000002
убыточных сделок : 117
успешных сделок : 55
maxVolInStrategy : 0.4
баланс стал таким : 126.05984000000000740');
