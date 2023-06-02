# Дипломная работа «Облачное хранилище»
## Описание проекта

REST-сервис для загрузки файлов и вывода списка уже загруженных файлов пользователя через разработанное третьими лицами веб-приложение [FRONT (Java Script)](https://github.com/netology-code/jd-homeworks/tree/master/diploma/netology-diplom-frontend).

Приложение подготовлено с использованием Spring Boot, maven. 

Для запуска используется docker, docker-compose: ``` docker-compose up ```

## Функционал
* Авторизация

Endpoint /login доступен для всех пользователей. При запуске приложения автоматически создается пользователь adim c паролем admin.
Endpoint /logout и все остальные (/file, /list) доступны только для авторизованных пользователей.

Авторизация осуществляется с использованием Spring Security и JWT токена, информация о пользователях хранится в БД postgreSQL. 

* Загрузка файла
* Удаление файла
* Вывод списка файлов
* Переименование файла
