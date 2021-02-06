FROM openjdk:8-jdk-alpine
MAINTAINER Caner Aydogan "contact@caner.com"

EXPOSE 8080
WORKDIR /usr/local/bin/

# for docker build
COPY ./target/MyDictionary-0.0.1-SNAPSHOT.jar MyDictionary-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "MyDictionary-0.0.1-SNAPSHOT.jar"]
