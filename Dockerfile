FROM nginx:alpine

RUN apk add --no-cache openjdk21-jre sqlite

RUN mkdir -p /ui /server /data

ENV SQLITE_DB_PATH=jdbc:sqlite:/data/iara.db
ENV JWT_SECRET=default_jwt_secret_please_change_in_production_environment

COPY iara-ui/dist /usr/share/nginx/html
COPY iara-server/build/libs/iara-server-*.jar /server/iara-server.jar

COPY ./config/nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

COPY ./config/entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

VOLUME /data

ENTRYPOINT ["/entrypoint.sh"]
