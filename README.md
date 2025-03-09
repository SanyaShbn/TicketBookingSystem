## Установка и запуск приложения

В этом кратком руководстве описывается процесс настройки и запуска приложения локально.
Для корректной работы приложения необходимо скачать и выболнить сборку другого проекта-микросервиса: ImageService.
Ссылка на репозиторий, где также есть гайд, как все корректно настроить и запустить: 

```
https://github.com/SanyaShbn/ImageService.git
```

Запуск контейнеров данного микросервиса необходимо выполнить обязательно ДО запуска основного сервиса!!!

### Установка

Сначала клонируйте репозиторий проекта:

```
https://github.com/SanyaShbn/TicketBookingSystem.git
```

В корневом каталоге проекта создайте файл с именем .env. Этот файл будет содержать переменные окружения, 
необходимые для работы приложения (убедитесь, что ваш текстовый редактор или IDE настроены для отслеживания изменений в файле .env).
Для запуска приложения необходимо настроить следующие переменные окружения:

- `KAFKA_BOOTSTRAP_SERVERS`: Сервер Kafka.
- `DB_URL`: URL базы данных.
- `DB_USERNAME`: Имя пользователя базы данных.
- `DB_PASSWORD`: Пароль базы данных.
- `POSTGRES_DB`: Имя базы данных Postgres.
- `IMAGE_SERVICE_URL`: URL сервиса изображений (второй микросервис, необходимый для корректной работы приложения).

Можете просто скопировать следующие настройки по умолчанию:

```
KAFKA_BOOTSTRAP_SERVERS=kafka:9092
DB_URL=jdbc:postgresql://db:5432/ticket-booking-system
DB_USERNAME=postgres
DB_PASSWORD=12345
POSTGRES_DB=ticket-booking-system
IMAGE_SERVICE_URL=http://image-service:8081/api/v1/images
```

Соберите проект локально, используя maven:

```
maven clean install
```

Перед запуском проекта через Docker Compose необходимо создать сеть (для корректной совместной работы контейнеров данного проекта и ImageService.
В README-файле репозитория второго проекта также есть этот шаг, если вы его уже выполняли, повторять нет необходимости):

```
docker network create backend-network
```

Для запуска docker-compose:

```
docker-compose up
```

### Использование

Тестирование API с использованием Swagger доступно по url (полноценный фронтенд еще в разработке,
возможно для тестирования функциональности с картинками более удобным будет Postman (для загрузки
файлов картинок)):

```
http://localhost:8080/swagger-ui/index.html
```

**Данные администратора (зарегистрироваться можно только в качестве обычного пользователя):**

username/email: admin@gmail.com

пароль: Admin_12345

## Описание процесса деплоя в kubernetes (на данный момент, инструкция - только для Windows)

### Предварительные требования

Убедитесь, что установлены следующие инструменты:

- [Minikube](https://minikube.sigs.k8s.io/docs/start/) (для локального Kubernetes-кластера)
- [kubectl](https://kubernetes.io/docs/tasks/tools/) (для управления кластером)
- Docker (с поддержкой локального демона Minikube)
- В качестве виртуальной машины использовал Oracle VirtualBox(https://www.virtualbox.org/wiki/Downloads)

### Шаг 1: Запуск Minikube

Запустите Minikube, если он ещё не запущен (в PowerShell):

```
minikube start --no-vtx-check
```

Настройте Docker-клиент на использование Docker-демона Minikube:

```
minikube docker-env | Invoke-Expression
```

### Шаг 2: Построение Docker-образа

Перейдите в корневую директорию проекта и выполните:

```
docker build -t ticketbookingsystem-spring-boot-app:latest .
```

### Шаг 3: PostgreSQL

Перейдите в корень проекта и используйте следующую команду для создания ConfigMap на основе содержимого папки initdb:

```
kubectl create configmap postgres-initdb --from-file=./initdb
```

Перейдите в директорию "kubernetes" в корне проекта, содержащую .yaml-файлы, необходимые для деплоя
проекта в kubernetes.
Примените файл persistent-volumes.yaml для настройки Persistent Volume в PostgreSQL:

```
kubectl apply -f persistent-volumes.yaml
```

Далее задеплойте PostgreSQL:

```
kubectl apply -f postgres-deployment.yaml
```

### Шаг 4: Деплой Kafka

Тестирование созданного и развернутого кластера рекомендуется производить совместно со вторым микросервисом,
ImageService(https://github.com/SanyaShbn/ImageService). По данной ссылке на репозиторий второго проекта доступен и
README с инструкцией по деплою в Docker и Kubernetes непосредственно этого второго проекта-микросервиса, которую стоит
выполнить перед переходом к остальным шагам данного гайда. Если же желаете работать только с текущим сервисом, все равно
рекомендую изучить README, доступный по предоставленной ранее ссылке, так как там описан, в том числе, процесс деплоя в Kubernetes
контейнера для Kafka. Этот шаг необходим для корректного деплоя данного проекта! Можете создать в директории 'kubernetes'
данного проекта .yaml-файлы с целью создания Deployment и Service для компонента Kafka. Затем скопировать туда код из файлов kafka-deployment.yaml и zookeeper-deployment.yaml второго проекта, а затем применить 
этот файл в kubectl (либо полностью развернуть второй проект, тогда компонент с Kafka уже будет присутствовать, и можете
приступать к дальнейшим шагам уже текущего гайда).

Рекомендуется периодически осуществлять проверку статуса подов, чтобы удостовериться, что все они запущены и корректно
работают перед развертыванием ImageService:

```
kubectl get pods
```

### Шаг 5: Деплой TicketBookingSystem

Примените файл spring-boot-app-deployment.yaml:

```
kubectl apply -f spring-boot-app-deployment.yaml
```

Перенаправьте порт для локального доступа после его запуска:

```
kubectl port-forward svc/spring-boot-app-service 8080:8080
```

Теперь тестирование сервиса доступно по адресу http://localhost:8080/swagger-ui/index.html.