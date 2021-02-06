FROM openjdk:8-jdk-alpine
MAINTAINER Caner Aydogan "contact@caner.com"

EXPOSE 8080
WORKDIR /usr/local/bin/

#for docker-compose build
#COPY ./myDictionary-GermanService/target/myDictionary-GermanService-1.0-SNAPSHOT.jar myDictionary-GermanService-1.0-SNAPSHOT.jar

# for docker build
COPY ./target/GermanService-1.0-SNAPSHOT.jar GermanService-1.0-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "GermanService-1.0-SNAPSHOT.jar"]
