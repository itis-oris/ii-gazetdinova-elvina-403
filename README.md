# Smart Closet — Docker-инфраструктура

Умный гардероб: приложение для управления одеждой с рекомендациями по цветовой совместимости.

---

## Архитектура

```
┌─────────────────────────────────────────────────────────────┐
│                        Docker Network                        │
│                                                             │
│   ┌──────────────┐   HTTP (синхронно)   ┌───────────────┐  │
│   │              │ ──────────────────── │               │  │
│   │  smart-closet│                      │ color-service │  │
│   │  (основное   │ ──────────────────── │ (микросервис  │  │
│   │  приложение) │   Kafka (асинхронно) │  цветов)      │  │
│   │   :8080      │                      │   :8081       │  │
│   └──────┬───────┘                      └───────┬───────┘  │
│          │                                      │          │
│          ▼                                      ▼          │
│   ┌──────────────┐   ┌──────────┐   ┌───────────────────┐  │
│   │  PostgreSQL  │   │  Redis   │   │  Kafka + Zookeeper │  │
│   │   :5432      │   │  :6379   │   │  :9092 / :29092   │  │
│   └──────────────┘   └──────────┘   └───────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

### Почему два канала связи между сервисами?

**HTTP → color-service (синхронный запрос)**
При отображении вещи в гардеробе основному приложению нужно мгновенно получить
читаемое название цвета (например, «Лазурный» вместо `#4DB8FF`). Это синхронная
операция — пользователь ждёт ответа, поэтому используется прямой HTTP-вызов
к REST API color-service.

**Kafka → color-service (асинхронные события)**
При сохранении новой вещи в гардероб основное приложение публикует событие
в Kafka-топик. color-service подписывается на этот топик и асинхронно
обрабатывает рекомендации по цветовой совместимости. Это не блокирует
пользователя: вещь сохраняется мгновенно, а рекомендации появляются позже.

---

## Быстрый старт

### Требования

- Docker Desktop 4.x+ или Docker Engine 24.x+
- Docker Compose v2 (входит в состав Docker Desktop)

### Запуск

```bash
# Клонируйте репозиторий и перейдите в корень проекта
cd smart-closet

# (Опционально) Создайте .env файл из примера и настройте переменные
cp .env.example .env

# Сборка образов и запуск всех сервисов
docker compose up --build

# Запуск в фоновом режиме
docker compose up --build -d
```

### Остановка

```bash
# Остановить контейнеры (данные PostgreSQL сохраняются)
docker compose down

# Остановить и удалить все данные (включая volume PostgreSQL)
docker compose down -v
```

---

## Сервисы и порты

| Сервис        | Образ / Сборка                    | Порт (хост) | Назначение                          |
|---------------|-----------------------------------|-------------|-------------------------------------|
| app           | `./smart-closet` (сборка)         | 8080        | Основное Spring Boot приложение     |
| color-service | `./color-service` (сборка)        | 8081        | Микросервис определения цветов      |
| postgres      | `postgres:16-alpine`              | 5432        | База данных PostgreSQL              |
| redis         | `redis:7-alpine`                  | 6379        | Кэш (сессии, запросы к color-svc)  |
| kafka         | `confluentinc/cp-kafka:7.5.0`     | 9092, 29092 | Брокер сообщений                    |
| zookeeper     | `confluentinc/cp-zookeeper:7.5.0` | —           | Координатор Kafka (внутренний)      |

---

## Swagger UI

| Сервис        | URL                                       |
|---------------|-------------------------------------------|
| smart-closet  | http://localhost:8080/swagger-ui.html     |
| color-service | http://localhost:8081/swagger-ui.html     |

---

## Администратор

| Поле     | Значение         |
|----------|------------------|
| Логин    | elvina_admin     |
| Пароль   | Elvinablossom28  |

---

## Переменные окружения

Основные переменные приложения (можно переопределить через `.env`):

| Переменная              | Значение по умолчанию             | Описание                              |
|-------------------------|-----------------------------------|---------------------------------------|
| `DB_URL`                | `jdbc:postgresql://postgres:5432/smartcloset` | JDBC URL базы данных      |
| `DB_USER`               | `elvina`                          | Пользователь PostgreSQL               |
| `DB_PASSWORD`           | `elvina_pass`                     | Пароль PostgreSQL                     |
| `REDIS_HOST`            | `redis`                           | Хост Redis                            |
| `REDIS_PORT`            | `6379`                            | Порт Redis                            |
| `KAFKA_BOOTSTRAP_SERVERS` | `kafka:29092`                   | Адрес Kafka-брокера                   |
| `COLOR_SERVICE_URL`     | `http://color-service:8081`       | Базовый URL color-service             |

---

## Структура проекта

```
smart-closet/                      ← корень проекта
├── docker-compose.yml             ← оркестрация всех сервисов
├── .env.example                   ← пример файла переменных окружения
├── .dockerignore                  ← глобальные исключения для Docker
├── README.md                      ← этот файл
│
├── smart-closet/                  ← основное Spring Boot приложение (порт 8080)
│   ├── Dockerfile                 ← multi-stage сборка (Maven → JRE)
│   ├── .dockerignore              ← исключения Docker для модуля
│   ├── pom.xml
│   └── src/
│       └── main/
│           ├── java/              ← исходный код Java
│           └── resources/
│               └── application.yml
│
└── color-service/                 ← микросервис цветов (порт 8081)
    ├── Dockerfile                 ← multi-stage сборка (Maven → JRE)
    ├── .dockerignore              ← исключения Docker для модуля
    ├── pom.xml
    └── src/
        └── main/
            ├── java/              ← исходный код Java
            └── resources/
                └── application.yml
```

---

## Полезные команды

```bash
# Посмотреть логи конкретного сервиса
docker compose logs -f app
docker compose logs -f color-service

# Пересобрать только один образ
docker compose build app
docker compose up -d app

# Подключиться к базе данных
docker compose exec postgres psql -U elvina -d smartcloset

# Подключиться к Redis CLI
docker compose exec redis redis-cli

# Просмотреть топики Kafka
docker compose exec kafka kafka-topics --bootstrap-server localhost:9092 --list
```
