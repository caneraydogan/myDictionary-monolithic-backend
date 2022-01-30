FROM openjdk:8-jdk-alpine
MAINTAINER Caner Aydogan "contact@caner.com"

EXPOSE 8081
WORKDIR /usr/local/bin/

# for docker build
COPY ./target/myDictionary-monolithic-be-0.0.1-SNAPSHOT.jar myDictionary-monolithic-be-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "myDictionary-monolithic-be-0.0.1-SNAPSHOT.jar"]
