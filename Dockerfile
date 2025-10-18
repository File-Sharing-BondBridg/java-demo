# Multi-stage Dockerfile for building and running the Spring Boot (Gradle) application

# Builder: use a full JDK image and the Gradle wrapper to produce the fat jar
FROM eclipse-temurin:17-jdk-jammy AS builder
WORKDIR /workspace

# Copy Gradle wrapper and project files (optimize layer caching)
COPY gradlew gradlew
COPY gradle gradle
COPY settings.gradle build.gradle ./
COPY src ./src

# Ensure the wrapper is executable and build the boot jar (skip tests for speed)
RUN chmod +x gradlew \
    && ./gradlew bootJar -x test --no-daemon

# Runtime: smaller JRE image
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copy the jar from the builder stage
COPY --from=builder /workspace/build/libs/*.jar app.jar

EXPOSE 8080

# Use a non-root user for better security
RUN addgroup --system app && adduser --system --ingroup app app
USER app

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
