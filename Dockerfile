# Step 1: Build Stage
FROM maven:3.9.4-eclipse-temurin-17 AS builder
WORKDIR /app

# Copy all modules
COPY . .

# Build all modules, skip tests
RUN mvn -B clean package -DskipTests

# Step 2: Runtime Stage
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app

# Copy final JAR from src module
COPY --from=builder /app/src/target/src-1.0.0.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]