FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY backend/pom.xml ./pom.xml
RUN --mount=type=cache,target=/root/.m2/repository mvn dependency:go-offline -B
RUN --mount=type=cache,id=maven-repo-cache,target=/root/.m2/repository mvn dependency:go-offline -B
COPY backend/. .
RUN --mount=type=cache,target=/root/.m2/repository mvn package -DskipTests -B
RUN --mount=type=cache,id=maven-repo-cache,target=/root/.m2/repository mvn package -DskipTests -B
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
ARG JAR_FILE=/app/target/find-course-0.0.1-SNAPSHOT.jar
COPY --from=build ${JAR_FILE} app.jar
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
