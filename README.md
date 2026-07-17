# Weather Viewer

Web-приложение для просмотра текущей погоды. Пользователь может зарегистрироваться, добавлять локации (города) и видеть их погоду на главной странице. Реализовано ручное управление сессиями и cookies (без Spring Security/Session).

---

## 🚀 Технологии

**Backend:** Java 21, Spring Framework 6.2 (MVC, ORM), Hibernate 6.6, Lombok, HikariCP, MapStruct, Caffeine  
**Database:** PostgreSQL, Flyway  
**JSON:** Jackson  
**Frontend:** Thymeleaf, Bootstrap 5  
**Логирование:** SLF4J + Logback  
**Тестирование:** JUnit 5, Mockito, Spring Tests, WireMock  
**Сборка:** Maven  
**Сервер:** Apache Tomcat 11  
**Деплой:** VPS (Ubuntu 24.04)

---

## 📦 Функциональность

- Регистрация / Авторизация / Logout с ручным управлением сессиями и cookies.
- Добавление, просмотр и удаление локаций (городов) с проверкой дубликатов по названию, стране и штату.
- Поиск локаций через OpenWeatherMap Geocoding API.
- Отображение текущей погоды (температура, ощущается как, влажность, описание, иконка) для всех добавленных локаций.
- Кэширование погоды (Caffeine) для снижения нагрузки на OpenWeather API.
- Шедуллер для автоматической очистки истекших сессий.
- Глобальная обработка ошибок с пользовательскими страницами и корректными HTTP-статусами.

---

## ⚙️ Переменные окружения

Для запуска приложения необходимо задать следующие переменные окружения.  
Значения, выделенные **жирным**, — это **примеры**, замените их на свои реальные данные.

### Обязательные переменные

| Переменная | Описание | Пример |
|------------|----------|--------|
| `DB_USERNAME` | Имя пользователя PostgreSQL | **`postgres`** |
| `DB_PASSWORD` | Пароль пользователя PostgreSQL | **`your_password`** |
| `OPENWEATHER_KEY` | API-ключ OpenWeatherMap | **`abc123...`** |

### Опциональные переменные (имеют значения по умолчанию)

| Переменная | Описание | Значение по умолчанию |
|------------|----------|-----------------------|
| `DB_URL` | URL подключения к БД | `jdbc:postgresql://localhost:5432/weather_db` |
| `SHOW_SQL` | Включить логи SQL-запросов | `false` |
| `DB_POOL_MAX_SIZE` | Максимальный размер пула соединений | `10` |
| `DB_POOL_MIN_IDLE` | Минимальное количество простаивающих соединений | `5` |

---

## 🔧 Запуск локально

1. Клонируйте репозиторий: git clone https://github.com/j0797/weather-viewer.git
2. Создайте базу данных PostgreSQL: CREATE DATABASE weather_db;
3. Установите переменные окружения: export DB_USERNAME=postgres, export DB_PASSWORD=your_password, export OPENWEATHER_KEY=your_openweather_key
4. Соберите WAR-архив: mvn clean package
5. Разверните на Tomcat: скопируйте target/weather-viewer.war в папку webapps вашего Tomcat. Запустите Tomcat (catalina.sh run или через IDE). После запуска приложение будет доступно по адресу: http://localhost:8080/

---

## 🌐 Демо

Проект будет доступен до **26.07.2026** по адресу:  
➡️ [http://45.153.189.92:8080/](http://45.153.189.92:8080/)

---

## 📬 Контакты

По вопросам доступа к API-ключу OpenWeatherMap или запуску проекта — пишите в Telegram: @jf0797

## 📄 Лицензия 

Проект выполнен в рамках учебного курса: [zhukovsd/java-backend-learning-course](https://zhukovsd.github.io/java-backend-learning-course/)
