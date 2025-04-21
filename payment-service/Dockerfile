# Step 1: Build the application using Gradle
FROM eclipse-temurin:17 AS builder

WORKDIR /app

# Copy only the necessary files first to leverage Docker caching
COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle settings.gradle ./
COPY src src

# Give execution permission to the Gradle Wrapper
RUN chmod +x gradlew

# Build the application without running testapps
RUN ./gradlew build -x test

# Step 2: Create the final image
FROM eclipse-temurin:17

LABEL maintainer="rajivrtk11@gmail.com"

WORKDIR /app

# Copy the built JAR from the builder stage
COPY --from=builder /app/build/libs/*.jar /app/PaymentService.jar

# Run the application
ENTRYPOINT ["java", "-jar", "/app/PaymentService.jar"]
