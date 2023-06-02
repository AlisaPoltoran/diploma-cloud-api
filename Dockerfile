FROM openjdk:17

COPY target/diploma-cloud-api-0.0.1-SNAPSHOT.jar myapp.jar

ENTRYPOINT ["java","-jar","/myapp.jar"]

#FROM maven:3.8.3-openjdk-17
#
#WORKDIR /springBootMessage
#COPY . .
#RUN mvn clean install
#
#CMD mvn spring-boot:run

