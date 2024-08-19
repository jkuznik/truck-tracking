FROM eclipse-temurin:21
LABEL authors="januszkuznik"

WORKDIR /truck_tracking

COPY . .

RUN wmvn clean package -Pprod

ENTRYPOINT ["java", "-jar", "/app.jar"]