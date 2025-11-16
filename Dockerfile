FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app

COPY . .

RUN mvn -B clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /com

COPY --from=builder /app/app/target/app-1.0.0.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]