#TicketBookingSystem (билеты на спортивные события)
##План разработки проекта (Java EE: Servlet + JSP)
**1. Анализ предметной области, проектирование архитектуры базы данных**
Шаг 1 – изучить аналоги для выявления основных фич (решил посмотреть ticketpro.by)
*Дедлайн: 28.11.2024*
Шаг 2 – сформировать структуру базы данных (выявить основные сущности и связи между ними).
*Дедлайн: 29.11.2024*
**2. Разбиение проекта на основные слои (dao/сервлеты/сервисы). Реализация базовых фич java web-приложения с использованием сервлетов**
Шаг 1 – реализация маппинга выявленных табличек их бд в сущности (entity)
Шаг 2 – реализация dao-классов для entity для взаимодействия с базой данных и реализации запросов под CRUD-операции на уровне базы данных
Шаг 3 – создание dto-классов, сервлетов и сервисов для реализации CRUD-операций над сущностями (как минимум добавление/изменение/удаление основных спортивных мероприятий и билетов на них – далее будет функциональностью администратора)
*Общий дедлайн для пункта – 30.11.2024*
**3. Реализация view слоя (jsp)**
Формирования макетов основных jsp-страниц для основных фич:
•	просмотр/добавление/изменение/удаление предстоящих спортивных событий и доступных билетов на них;
•	выбор события, времени, места и оформление покупки билета;
•	страницы авторизации/регистрации.
*Дедлайн: 01.12.2024*
**4. Более подробно по вичам.**
Шаг 1 – каталог мероприятий/спортивных событий, фильтры/сортировка/пагинация
*Дедлайн: 04.12.2024*
Шаг 2 – выбор билетов на мероприятие: сектор, место (несколько билетов/мест), сортировка по стоимости (хотел бы попробовать сделать простенькую визуализация стадиона/зала, где уже билеты будут разбиты по секторам, указана стоимость места, цвет места сигнализирует свободно оно или нет)
*Дедлайн: 08.12.2024*
Шаг 3 – оформление покупки/отмена покупки
*Дедлайн: 10.12.2024*
Шаг 4 – обдумывание доп. функционала (скидки/акции на билеты, добавление картинок в каталог мероприятий (например, какие-то баннеры/постеры мог бы добавлять администратор))
*Дедлайн: 11.12.2024*
**5. Авторизация/регистрация**
Добавление авторизации/регистрации пользователей, разбиение функционала на обычного юзера и администратора. Администратор – работа с каталогами мероприятий и билетов на них. Юзер – просмотр доступных мероприятий/билетов, оформление покупки
*Дедлайн: 12.12.2024*
