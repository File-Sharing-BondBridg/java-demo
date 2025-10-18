# Multi-stage Dockerfile for building and running the Spring Boot (Gradle) application

# Builder: use the official Gradle image (includes Gradle) so we don't depend on a
# checked-in gradle-wrapper.jar. This avoids errors on hosts where the wrapper jar
# isn't present in the build context (e.g. Render.com).
FROM gradle:8.6-jdk17 AS builder
WORKDIR /home/gradle/project

# Copy project files. Set ownership to the `gradle` user inside the image for proper permissions.
COPY --chown=gradle:gradle . .

# Build the boot jar (skip tests for speed). Using the image's Gradle avoids needing gradle-wrapper.jar.
RUN gradle bootJar -x test --no-daemon

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
