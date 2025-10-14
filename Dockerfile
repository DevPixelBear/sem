#FROM openjdk:latest
#COPY ./target/devops-0.1.0.1-jar-with-dependencies.jar /tmp
#WORKDIR /tmp
#ENTRYPOINT ["java", "-jar", "devops-0.1.0.1-jar-with-dependencies.jar"]


# 1️⃣ Use Maven image to build the project
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app
COPY pom.xml .
COPY src ./src

# Build the jar file (skips tests for faster build)
RUN mvn clean package -DskipTests

# 2️⃣ Use a smaller runtime image for the final container
FROM openjdk:17-jdk-slim
WORKDIR /app

# Copy the built JAR file from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Set environment variables for DB connection (optional)
ENV DB_HOST=db
ENV DB_PORT=3306
ENV DB_USER=root
ENV DB_PASSWORD=root
ENV DB_NAME=world

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]
