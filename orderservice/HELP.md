# Getting Started

Инструкция по запуску:
Запустите PostgreSQL:

```bash
docker-compose up -d
```
Запустите приложение:

```bash
mvn spring-boot:run
```

Примеры запросов:

Создать заказ:

```bash
curl -X POST http://localhost:8080/api/orders \
-H "Content-Type: application/json" \
-d '{"userId": 123, "productName": "Laptop", "quantity": 1}'
```

Получить заказы пользователя:

```bash
curl http://localhost:8080/api/orders/user/123
```
Получить все заказы:

```bash
curl http://localhost:8080/api/orders
```
Это базовая версия без шардирования.
После того как вы убедитесь, что всё работает, мы перейдём к добавлению шардирования с двумя контейнерами PostgreSQL.