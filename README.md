# Дипломная работа «Облачное хранилище»
## Описание проекта

REST-сервис для загрузки файлов и вывода списка уже загруженных файлов пользователя через разработанное третьими лицами веб-приложение [FRONT (Java Script)](https://github.com/netology-code/jd-homeworks/tree/master/diploma/netology-diplom-frontend).

Приложение подготовлено с использованием Spring Boot, Spring Security, Flyway, PostgreSQL, JSON Web Token (JWT), Lombok, Hibernate. 

Для запуска используется docker, docker-compose: ``` docker-compose up ```

При запуске приложения автоматически создается пользователь admin c паролем admin.

## Функционал
* Авторизация

Endpoint /login доступен для всех пользователей. Endpoint /logout и все остальные (/file, /list) доступны только для авторизованных пользователей.

Авторизация осуществляется с использованием Spring Security и JWT токена, информация о пользователях хранится в БД PostgreSQL. 

* Загрузка файла
* Удаление файла
* Вывод списка файлов
* Переименование файла

## Логирование
На уровне Service реализовано логирование с использованием @Slf4j. Логи сохраняются в logs/cloud.log.