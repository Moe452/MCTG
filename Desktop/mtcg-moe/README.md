# Monster Trading Card Game (MTCG)

## How to run
### Docker Container for postgres DB
````
docker run --name mtcg -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=mtcg -p 5432:5432 -d postgres
````
### Create SQL Tables
````
cat ./database.sql | docker exec -i mtcg psql -U postgres -d mtcg
````

### Run Application
afterwards just run Main.java

## Contact

MTCG by Mohamed El Moghazy
