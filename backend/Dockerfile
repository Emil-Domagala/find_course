# FROM maven:3.9.9-eclipse-temurin-21 AS builder
# WORKDIR /app
# COPY pom.xml .
# RUN mvn dependency:go-offline -B
# COPY src ./src
# RUN mvn clean package

# FROM openjdk:21-slim AS runner
# WORKDIR /app
# COPY --from=builder ./app/target/*.jar ./app.jar
# EXPOSE 8080
# ENTRYPOINT ["java", "-jar", "app.jar"]

FROM openjdk:21-slim
RUN addgroup --system app && adduser --system --group app
USER app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]


# docker buildx build --platform linux/amd64,linux/arm64 -t emildomagalaa/find_course_java:latest --push .
# docker run -it -p 8080:8080 --env-file ./.env emildomagalaa/find_course_java:latest    