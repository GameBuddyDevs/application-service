FROM openjdk:17-jdk-alpine
COPY build/libs/application-service-0.0.1-SNAPSHOT.jar application-service-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/application-service-0.0.1-SNAPSHOT.jar"]