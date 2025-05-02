FROM openjdk:21-slim
RUN addgroup --system app && adduser --system --group app
USER app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
