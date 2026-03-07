# Media Tracker Bot

Telegram-бот для ведения личного списка фильмов/сериалов и заметок о просмотре. Проект — учебный MVP с аккуратной доменной моделью, интеграцией с PostgreSQL и заготовками под Kinopoisk API.

## Что уже есть

- доменная модель: пользователи, медиа, статусы просмотра, отзывы, персоны и роли
- схема БД и автозагрузка в Postgres через Docker Compose
- конфигурация приложения через `application.properties`
- каркас запуска в `com.indistudia.Main`

## Стек

- Java 25
- Maven
- Hibernate ORM
- PostgreSQL 16
- Telegram Bots API SDK

## Быстрый старт

1. Поднять базу данных:

```bash
docker compose up -d
```

2. Настроить параметры в `src/main/resources/application.properties`:

- `telegram.bot.token`
- `telegram.bot.username`
- `kinopoisk.base-url`
- `kinopoisk.api-key`
- `db.url`
- `db.username`
- `db.password`

3. Запустить приложение из IDE, указав `com.indistudia.Main` как entry point.

## База данных

Docker Compose автоматически применяет схему из `src/main/resources/db/schema.sql` и создает таблицы:

- `users`
- `media`
- `watch_entries`
- `reviews`
- `persons`
- `media_person_roles`

## Структура проекта

- `src/main/java/com/indistudia/config` — конфиги Telegram / БД / Hibernate / Kinopoisk
- `src/main/java/com/indistudia/domain` — сущности доменной модели
- `src/main/java/com/indistudia/repository` — репозитории доступа к данным
- `src/main/resources` — `application.properties` и SQL-схема