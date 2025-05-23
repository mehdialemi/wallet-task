FROM amazoncorretto:21-alpine

WORKDIR /app

ARG JAR_FILE=target/simplewallet-0.0.1-SNAPSHOT.jar
COPY $JAR_FILE app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
