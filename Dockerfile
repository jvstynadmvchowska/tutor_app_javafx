FROM mysql:latest

ENV MYSQL_ROOT_PASSWORD=root

COPY database_mysql_on_docker.sql /docker-entrypoint-initdb.d/
