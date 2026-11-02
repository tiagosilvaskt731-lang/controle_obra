# Estágio 1: Compilar o código Java com Maven
FROM maven:3.8.5-openjdk-17 AS build
COPY . .
RUN mvn clean package -DskipTests

# Estágio 2: Rodar a aplicação Java Spring Boot
FROM openjdk:17-jdk-slim
COPY --from=build /target/controle_obra-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]