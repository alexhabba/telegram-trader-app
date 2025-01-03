create index if not exists idx_create_date_symbol on bot.bar (symbol, create_date);

ALTER TABLE bot.bar ALTER COLUMN vol_buy TYPE DOUBLE PRECISION USING vol_buy::DOUBLE PRECISION;
ALTER TABLE bot.bar ALTER COLUMN vol_sell TYPE DOUBLE PRECISION USING vol_sell::DOUBLE PRECISION;
