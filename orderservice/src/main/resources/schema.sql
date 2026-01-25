CREATE TABLE IF NOT EXISTS orders (
    id UUID PRIMARY KEY,
    user_id BIGINT NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    quantity INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL
    );

ALTER TABLE users
    ALTER COLUMN id TYPE BIGINT,
    ALTER COLUMN id SET NOT NULL,
    ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY;

-- ИЛИ способ 2: Удалить и пересоздать таблицу
DROP TABLE IF EXISTS users;

CREATE TABLE IF NOT EXISTS users (
                       id BIGSERIAL PRIMARY KEY,
                       name VARCHAR(255) NOT NULL,
                       first_name VARCHAR(255) NOT NULL,
                       age INT NOT NULL,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE users ADD CONSTRAINT email_check
    CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$')
    NOT VALID;  -- NOT VALID чтобы не проверять существующие данные