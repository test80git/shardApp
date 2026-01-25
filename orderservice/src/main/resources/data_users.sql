-- -- Очищаем таблицу (опционально)
-- TRUNCATE TABLE orders CASCADE;

-- Заполняем тестовыми данными
INSERT INTO users (name, first_name, age, email, created_at)
VALUES
    ('Vova', 'Volk', 11, 'vova@ya.ru',  '2024-01-10 10:30:00'),
    ('VovaV','Volkov', 16, 'vovaV@ya.ru',  '2025-01-10 10:30:00'),
    ('Olya', 'Lol',23,'olya@google.com',  '2026-01-10 10:30:00');

-- -- Проверяем
SELECT COUNT(*) as total_orders FROM users;