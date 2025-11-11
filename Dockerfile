# Step 1: Build stage
FROM maven:3.9.4-eclipse-temurin-17 AS builder
WORKDIR /app

# Copy everything
COPY . .

# Build the project (this runs mvn clean package)
RUN mvn -B clean package -DskipTests

# Step 2: Runtime stage
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app

# Copy the built JAR from the previous stage
COPY --from=builder /app/src/target/src-1.0.0.jar app.jar

# Expose port
EXPOSE 8080

# Run the JAR
ENTRYPOINT ["java", "-jar", "app.jar"]