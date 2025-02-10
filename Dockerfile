FROM gradle:latest AS builder

RUN apt-get update && apt-get install -y ffmpeg && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY build.gradle.kts settings.gradle.kts ./
COPY src ./src

RUN gradle build --no-daemon

FROM openjdk:17-jdk-slim

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]