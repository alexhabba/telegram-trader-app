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

INSERT INTO bot."parameter"
(id, symbol, sl, tp, vol, "minute", shift, coefficient, strategy, description)
VALUES('f5e7e73c-f111-40ca-b815-575c8649b6a2'::uuid, 'AAVE', 1.9, 6.7, 600.0, 34, 0.9, 1.1, 1, 'strategy = 1 контракт = 3
commonResult : 2099
result : 505.4673299999999
убыточных сделок : 247
успешных сделок : 104
maxVolInStrategy : 8.559350118330006');

INSERT INTO bot."parameter"
(id, symbol, sl, tp, vol, "minute", shift, coefficient, strategy, description)
VALUES('06299b95-5bcd-4c21-859e-9958ca40fb0d'::uuid, 'AAVE', 1.7, 5.5, 1000.0, 13, 0.5, 1.1, 7, 'strategy = 7 контракт = 3
commonResult : 1500
result : 399.57523999999995
убыточных сделок : 282
успешных сделок : 123
maxVolInStrategy : 13.784918959071664');

INSERT INTO bot."parameter"
(id, symbol, sl, tp, vol, "minute", shift, coefficient, strategy, description)
VALUES('49c08ab4-f01a-4b61-87d9-b356c5492ec6'::uuid, 'SOL', 1.9, 2.5, 3800.0, 22, 0.9, 1.1, 8, NULL);

INSERT INTO bot."parameter"
(id, symbol, sl, tp, vol, "minute", shift, coefficient, strategy, description)
VALUES('f831116a-7381-4e27-a148-85300d97de35'::uuid, 'SOL', 1.9, 1.7, 800.0, 34, 0.5, 1.0, 1, 'strategy = 1 контракт = 3
commonResult : 1240
result : 415.0135799999996
убыточных сделок : 386
успешных сделок : 566
maxVolInStrategy : 3.0');
