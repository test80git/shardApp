## 1. SQL скрипт для заполнения таблицы orders

**data.sql** (создай в корне проекта или в папке `src/main/resources/`)
```sql
-- Очищаем таблицу (опционально)
TRUNCATE TABLE orders CASCADE;

-- Заполняем тестовыми данными
INSERT INTO orders (id, user_id, product_name, quantity, created_at) VALUES
    ('550e8400-e29b-41d4-a716-446655440001', 101, 'Ноутбук Lenovo', 1, '2024-01-10 10:30:00'),
    ('550e8400-e29b-41d4-a716-446655440002', 101, 'Мышь беспроводная', 2, '2024-01-11 14:20:00'),
    ('550e8400-e29b-41d4-a716-446655440003', 102, 'Смартфон iPhone', 1, '2024-01-12 09:15:00'),
    ('550e8400-e29b-41d4-a716-446655440004', 103, 'Наушники Sony', 3, '2024-01-13 16:45:00'),
    ('550e8400-e29b-41d4-a716-446655440005', 104, 'Клавиатура механическая', 1, '2024-01-14 11:10:00'),
    ('550e8400-e29b-41d4-a716-446655440006', 105, 'Монитор 27"', 2, '2024-01-15 13:25:00'),
    ('550e8400-e29b-41d4-a716-446655440007', 101, 'Коврик для мыши', 1, '2024-01-16 17:30:00'),
    ('550e8400-e29b-41d4-a716-446655440008', 106, 'Внешний жесткий диск 1TB', 1, '2024-01-17 10:00:00'),
    ('550e8400-e29b-41d4-a716-446655440009', 102, 'Чехол для телефона', 2, '2024-01-18 15:45:00'),
    ('550e8400-e29b-41d4-a716-446655440010', 107, 'USB-C кабель', 5, '2024-01-19 12:20:00');

-- Проверяем
SELECT COUNT(*) as total_orders FROM orders;
```

## 2. Способ 1: Через Docker (рекомендуется)

**Добавь в docker-compose.yml**:
```yaml
version: '3.8'

services:
  postgres:
    image: postgres:16-alpine
    container_name: order-postgres-single
    environment:
      POSTGRES_DB: order_db
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    ports:
      - "5432:5432"
    volumes:
      - postgres_data_single:/var/lib/postgresql/data
      # Монтируем SQL скрипт в папку инициализации
      - ./sql/init.sql:/docker-entrypoint-initdb.d/init.sql
      - ./sql/data.sql:/docker-entrypoint-initdb.d/02-data.sql
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U postgres"]
      interval: 10s
      timeout: 5s
      retries: 5
    restart: unless-stopped

volumes:
  postgres_data_single:
```

**Создай папку `sql/` и файлы**:

**sql/init.sql** (создание таблицы):
```sql
-- Этот файл выполнится первым
CREATE TABLE IF NOT EXISTS orders (
    id UUID PRIMARY KEY,
    user_id BIGINT NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    quantity INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL
);
```

**sql/data.sql** (заполнение данными):
```sql
-- Этот файл выполнится вторым (из-за префикса 02-)
INSERT INTO orders (id, user_id, product_name, quantity, created_at) 
SELECT * FROM (VALUES
    ('550e8400-e29b-41d4-a716-446655440001', 101, 'Ноутбук Lenovo', 1, '2024-01-10 10:30:00'),
    ('550e8400-e29b-41d4-a716-446655440002', 101, 'Мышь беспроводная', 2, '2024-01-11 14:20:00'),
    ('550e8400-e29b-41d4-a716-446655440003', 102, 'Смартфон iPhone', 1, '2024-01-12 09:15:00'),
    ('550e8400-e29b-41d4-a716-446655440004', 103, 'Наушники Sony', 3, '2024-01-13 16:45:00'),
    ('550e8400-e29b-41d4-a716-446655440005', 104, 'Клавиатура механическая', 1, '2024-01-14 11:10:00')
) AS data(id, user_id, product_name, quantity, created_at)
WHERE NOT EXISTS (SELECT 1 FROM orders LIMIT 1); -- Вставляем только если таблица пустая
```

## 3. Способ 2: Через Spring Boot (data.sql)

**Положи файлы в `src/main/resources/`**:

**schema.sql**:
```sql
CREATE TABLE IF NOT EXISTS orders (
    id UUID PRIMARY KEY,
    user_id BIGINT NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    quantity INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL
);
```

**data.sql** (Spring Boot запустит его автоматически):
```sql
INSERT INTO orders (id, user_id, product_name, quantity, created_at) 
VALUES 
    (gen_random_uuid(), 101, 'Ноутбук Lenovo', 1, CURRENT_TIMESTAMP - INTERVAL '5 days'),
    (gen_random_uuid(), 101, 'Мышь беспроводная', 2, CURRENT_TIMESTAMP - INTERVAL '4 days'),
    (gen_random_uuid(), 102, 'Смартфон iPhone', 1, CURRENT_TIMESTAMP - INTERVAL '3 days');
```

**Настрой в application.yml**:
```yaml
spring:
  sql:
    init:
      mode: always  # Всегда инициализировать
      schema-locations: classpath:schema.sql
      data-locations: classpath:data.sql
  jpa:
    hibernate:
      ddl-auto: validate  # Проверяет, но не создает
    defer-datasource-initialization: true
```

## 4. Способ 3: Через Liquibase/Flyway (более продвинуто)

**Добавь зависимость в pom.xml**:
```xml
<dependency>
    <groupId>org.liquibase</groupId>
    <artifactId>liquibase-core</artifactId>
</dependency>
```

**db/changelog/db.changelog-master.xml**:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<databaseChangeLog
    xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog
    http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-4.9.xsd">
    
    <changeSet id="1" author="yourname">
        <createTable tableName="orders">
            <column name="id" type="UUID">
                <constraints primaryKey="true" nullable="false"/>
            </column>
            <column name="user_id" type="BIGINT">
                <constraints nullable="false"/>
            </column>
            <column name="product_name" type="VARCHAR(255)">
                <constraints nullable="false"/>
            </column>
            <column name="quantity" type="INTEGER">
                <constraints nullable="false"/>
            </column>
            <column name="created_at" type="TIMESTAMP">
                <constraints nullable="false"/>
            </column>
        </createTable>
    </changeSet>
    
    <changeSet id="2" author="yourname">
        <loadData tableName="orders" file="db/data/orders.csv">
            <column name="id" type="UUID"/>
            <column name="user_id" type="NUMERIC"/>
            <column name="product_name" type="STRING"/>
            <column name="quantity" type="NUMERIC"/>
            <column name="created_at" type="TIMESTAMP"/>
        </loadData>
    </changeSet>
</databaseChangeLog>
```

## 5. Быстрый запуск с Docker (рекомендую)

```bash
# 1. Создай структуру папок
mkdir -p sql

# 2. Создай файлы init.sql и data.sql как выше

# 3. Перезапусти контейнер
docker-compose down -v
docker-compose up -d

# 4. Проверь данные
docker exec -it order-postgres-single psql -U postgres -d order_db -c "SELECT * FROM orders LIMIT 5;"
```

**Что происходит:**
- При первом запуске Docker монтирует SQL файлы в `/docker-entrypoint-initdb.d/`
- PostgreSQL выполняет их в алфавитном порядке
- `01-init.sql` → создает таблицу
- `02-data.sql` → заполняет данными
- При повторном запуске данные сохраняются в volume

Выбери способ 1 или 2 в зависимости от твоих предпочтений.