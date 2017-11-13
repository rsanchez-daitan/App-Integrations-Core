FROM openjdk:8u131-jdk-alpine

RUN apk add --no-cache bash

COPY ./integration-web/target/integration.jar /opt/symphony/ib/integration.jar
COPY ./target/libs/*.jar                      /opt/symphony/ib/libs/

ENV LOADER_PATH /opt/symphony/ib/libs/
ENV SERVER_ADDRESS 0.0.0.0
ENV SERVER_PORT 80
ENV LOG4J2_OUTPUT_CONSOLE true

EXPOSE 80

WORKDIR /opt/symphony/ib

ENTRYPOINT [ "java", "-Xms512m", "-Xmx2g", "-jar", "/opt/symphony/ib/integration.jar" ]
