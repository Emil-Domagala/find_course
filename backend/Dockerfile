FROM openjdk:21-slim
RUN addgroup --system app && adduser --system --group app
USER app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]


# docker buildx build --platform linux/amd64,linux/arm64 -t emildomagalaa/find_course_java:latest --push .
# docker run -it -p 8080:8080 --env-file ./.env emildomagalaa/find_course_java:latest    