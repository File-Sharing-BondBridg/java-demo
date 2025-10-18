# Multi-stage Dockerfile for building and running the Spring Boot (Gradle) application

# Builder: Use official Gradle image with JDK 17
FROM gradle:8.14.3-jdk17 AS builder
WORKDIR /home/gradle/project

# Copy project files with proper permissions
COPY --chown=gradle:gradle . .

# Debug: List directory to verify files
RUN ls -la /home/gradle/project && ls -la /home/gradle/project/gradle/wrapper || echo "Wrapper directory missing"

# Ensure gradlew is executable and build the JAR
RUN chmod +x gradlew && ./gradlew clean bootJar -x test --no-daemon

# Debug: Verify JAR exists
RUN ls -la /home/gradle/project/build/libs || echo "No JAR found in build/libs"

# Runtime: Smaller JRE image
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copy the JAR from the correct build path
COPY --from=builder /home/gradle/project/build/libs/*.jar app.jar

EXPOSE 8080

# Use non-root user for security
RUN addgroup --system app && adduser --system --ingroup app app
USER app

ENTRYPOINT ["java", "-jar", "/app/app.jar"]