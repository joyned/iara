FROM amazoncorretto:21.0.8-alpine3.22

RUN apk add --no-cache sqlite

ENV SQLITE_DB_PATH=jdbc:sqlite:/data/iara.db

RUN mkdir -p /data

RUN sqlite3 /data/iara.db "VACUUM;"

COPY build/libs/iara-server-*.jar /opt/iara-server.jar

EXPOSE 8080

ENTRYPOINT exec java -jar /opt/iara-server.jar