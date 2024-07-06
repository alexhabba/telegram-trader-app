CREATE TABLE telegram_user
(
    chat_id                   int8        NOT NULL,
    first_name                varchar(30) NULL,
    last_name                 varchar(30) NULL,
    registered_at             timestamp   NULL,
    user_name                 varchar(30) NULL,
    "role"                    varchar(20) NULL,
    CONSTRAINT telegram_user_pkey PRIMARY KEY (chat_id)
);