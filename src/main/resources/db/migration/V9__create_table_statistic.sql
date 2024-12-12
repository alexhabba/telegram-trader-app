CREATE TABLE statistic
(
    id                  UUID PRIMARY KEY,
    max_vol_in_strategy DOUBLE PRECISION,
    min                 INT,
    vol                 DOUBLE PRECISION,
    shift               DOUBLE PRECISION,
    sl                  DOUBLE PRECISION,
    tp                  DOUBLE PRECISION,
    strategy            VARCHAR(255),
    bad_count           DOUBLE PRECISION,
    success_count       DOUBLE PRECISION,
    result              DOUBLE PRECISION,
    common_result       DOUBLE PRECISION
);
