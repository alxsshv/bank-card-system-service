FROM eclipse-temurin:21-jre-alpine
ARG JAR_FILE=./build/libs/bank-card-system-service-1.0.0.jar
ARG RESOURCES=./build/resources
WORKDIR /usr/src/app/
COPY ${JAR_FILE} /usr/src/app/bank-card-system-service-1.0.0.jar
ADD ${RESOURCES} /usr/src/app/
ADD ./build/resources/main/application.yml /usr/src/app/
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=docker", "/usr/src/app/bank-card-system-service-1.0.0.jar"]