# ─── Stage 1: Build ────────────────────────────────────────────────
# We use a two-stage build. This is a production best practice.
#
# Stage 1 (builder): Uses a full JDK image to compile and package the app.
# Stage 2 (runtime): Uses a minimal JRE image to actually run it.
#
# Why two stages?
# The JDK (compiler) is ~600MB. The JRE (runtime) is ~200MB.
# We don't need the compiler in production — just the runtime.
# Final image size: ~200MB instead of ~600MB.
FROM eclipse-temurin:21-jdk-alpine AS builder

# Set working directory inside the container
WORKDIR /app

# Copy Maven wrapper and pom.xml first (before source code)
# Why? Docker caches layers. If pom.xml hasn't changed,
# Docker reuses the cached dependency download layer.
# Only when pom.xml changes does it re-download dependencies.
# This makes subsequent builds much faster.
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Download dependencies (cached unless pom.xml changes)
RUN ./mvnw dependency:go-offline -B

# copy source code and build
COPY src src
RUN ./mvnw package -DskipTests -B
# -DskipTests: don't run tests during Docker build
# -B: batch mode (no interactive prompts)

# ─── Stage 2: Runtime ──────────────────────────────────────────────
# Start fresh with a minimal JRE image
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy ONLY the built JAR from Stage 1
# The compiler, source code, and Maven cache stay behind
COPY --from=builder /app/target/*.jar app.jar

# Document which port the app listens on
# (doesn't actually publish the port — docker-compose handles that)
EXPOSE 8080

# The command that runs when the container starts
# We activate the "docker" Spring profile (explained in Step 5.2)
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=docker", "app.jar"]





