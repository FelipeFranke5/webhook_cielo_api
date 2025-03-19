FROM eclipse-temurin:17-alpine
RUN mkdir /opt/app
COPY target/webhook_cielo_api-0.0.1-SNAPSHOT.jar /opt/app/app.jar
CMD ["java", "-jar", "/opt/app/app.jar"]