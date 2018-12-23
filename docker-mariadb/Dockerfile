FROM mariadb:latest

ENV MYSQL_ROOT_PASSWORD=root
ENV MYSQL_DATABASE=cardManagerDB

COPY *.sh /docker-entrypoint-initdb.d/

COPY *.sql /docker-entrypoint-initdb.d/
