FROM amazoncorretto:21.0.8-alpine3.22

RUN mkdir -p /data

COPY build/libs/iara-server-*.jar /opt/iara-server.jar

EXPOSE 8080

ENTRYPOINT exec java -jar /opt/iara-server.jar