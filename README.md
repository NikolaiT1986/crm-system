# CRM System

Упрощённая CRM-система для управления продавцами и их транзакциями, а также аналитики продаж.
Проект реализован на Java 21, Spring Boot 3, PostgreSQL 17, с покрытием тестами (JUnit 5, RestAssured, Testcontainers) и
возможностью отчётов процента покрытия (JaCoCo).

## Системные требования

- Java 21+
- Docker
- Git (опционально)
- PostgreSQL 17 (опционально)

## Установка

Клонируйте репозиторий

```bash
  git clone https://github.com/NikolaiT1986/crm-system.git
  cd crm-system
```

Или скачайте и распакуйте ZIP-архива и перейдите в папку проекта.

## Настройка окружения

Создайте схему базы данных в локальном PostgreSQL или используйте Docker и выполните команду:

```bash
  docker run -d \
  --name crm-system-db \
  -p 5432:5432 \
  -e POSTGRES_USER=<username> \
  -e POSTGRES_PASSWORD=<password> \
  -e POSTGRES_DB=srm_system \
  --restart unless-stopped \
  postgres:17-alpine
```

Создайте файл `resources/secrets/secret.properties` со следующим содержимым:

```properties
db.url=jdbc:postgresql://<host>:<port>/<database>
db.username=<username>
db.password=<password>
```

или используйте переменные окружения

## Запуск

Запустить приложение:

```bash
  ./gradlew bootRun
```

Запуск тестов:

```bash
  ./gradlew test
```

Swagger UI станет доступен по адресу <http://localhost:8080/swagger-ui.html>

Отчёт покрытия JaCoCo:

```bash
  ./gradlew jacocoTestReport
```

После выполнения отчёт будет доступен по пути `build/reports/jacoco/test/html/index.html`

#### Объяснение реализации алгоритм для определения наилучшего периода времени для продавца

В качестве параметра эффективности была выбрана сумма по всем совершённым транзакциям за период, т.к. это мне показалось
более логичным, чем их количество. Количество транзакций за самый продуктивный период выводится дополнительно в
качестве статистики.