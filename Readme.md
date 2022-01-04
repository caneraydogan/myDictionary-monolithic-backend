#Local development:

##1. Postgres
Run the postgres container

`docker-compose up -d postgres`

Create tables

`Run full_schema.ddl`

Run the application


#Build and push docker image:

docker build -t ceqo/dictionary-monolithic-be .

docker push ceqo/dictionary-monolithic-be


##Frontend

