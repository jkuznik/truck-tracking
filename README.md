![fireTmsLogo](/src/main/resources/static/mainLogo.382e4c42.svg)
# TRUCK TRACKING
 
## Description
#### Truck tracking is my implementation of a recruitment task for Fire TMS. <br>The application is configured to run on two different profiles: test and prod.

## How to run
#### The simplest way to demonstrate the application's functionality is to clone this project and run it locally with the 'test' profile configuration using an IDE. <br><br> The configuration profile 'prod' requires a connection to a configured PostgreSQL database for testing. Using IntelliJ and Docker, this can be easily achieved by setting the application's active profile to 'prod' and simply running the application with the Docker daemon running in the background.

## App
#### The application running in the 'prod' environment is secured by Spring Security using BasicAuth with the user:<br>login: ***fire***<br>password: ***tms***<br><br>In the 'test' environment security is fully unlocked<br><br>The application addresses the challenges outlined in the recruitment task, and Swagger is used for visualizing its functionality. The interface is available at the endpoint ~/swagger-ui/index.html. <br><br>During the application startup, the database is populated with several test entries using Liquibase migrations. Additionally, for demonstrating the search functionality and pagination, the application includes a special Bootstrap class that can populate the database with any number of entries containing random values while maintaining the required application logic.

## Technology Stack
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=flat&logo=spring-boot&logoColor=white)
![Java](https://img.shields.io/badge/Java-007396?style=flat&logo=java&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=flat&logo=postgresql&logoColor=white)
![IntelliJ IDEA](https://img.shields.io/badge/IntelliJ%20IDEA-000000?style=flat&logo=intellij-idea&logoColor=purple)
![QueryDSL](https://img.shields.io/badge/QueryDSL-2C2D72?style=flat&logo=java&logoColor=white)
![Liquibase](https://img.shields.io/badge/Liquibase-2962FF?style=flat&logo=liquibase&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=flat&logo=docker&logoColor=white)
![Swagger](https://img.shields.io/badge/Swagger-85EA2D?style=flat&logo=swagger&logoColor=white)

## TODO
#### 1. Increase test coverage for the truck domain
#### 2. Add a GIF to the README to demonstrate the main features of the application

## About me
#### Nazywam się Janusz Kuźnik, mam 35 lat i od ponad roku uczę się tworzyć oprogramowanie z nastawieniem pracy jako programista w przyszłości.
#### Projekt "Truck Tracking" przesyłam Państwu poza rekrutacją jako przykładowe rozwiązanie założeń jednego z zadań rekrutacyjnych firmy Fire TMS. Jednocześnie traktuję to jako formę prezentacji mojej osoby wraz z oczywistą chęcią z mojej strony na podjęcia współpracy w przyszłości.
#### Zachęcam do kontaktu ze mną, z przyjemnością opowiem o realizacji nieniejszego projektu.
#### ***janusz.kuznik89@gmail.com***
