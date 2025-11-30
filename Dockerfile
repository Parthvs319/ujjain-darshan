FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app

#COPY pom.xml .
#COPY helpers ./helpers
#COPY hotel ./hotel
#COPY models ./models
#COPY user ./user
#COPY auth ./auth
#COPY app ./app

COPY . .

RUN mvn -q -DskipTests package

FROM eclipse-temurin:17-jdk
WORKDIR /app

COPY --from=builder /app/app/target/app-1.0.0.jar app.jar

EXPOSE 8080
CMD ["java", "-jar", "app.jar"]