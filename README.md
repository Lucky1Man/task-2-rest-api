## To run this project you need to provide configurations via environment variables or program arguments </br>
DB_ADDRESS(without port as it is defaulted to 5432)</br>
DB_NAME</br>
DB_USERNAME</br>
DB_PASSWORD</br>
## Example for program arguments:
` --DB_ADDRESS=localhost --DB_NAME=profitsoft_rest_api --DB_USERNAME=postgres --DB_PASSWORD=postgres `
## When you start the project you can check this page http://localhost:8080/swagger-ui/index.html#/ to see the endpoint descriptions
![image](https://github.com/Lucky1Man/task-2-rest-api/assets/86126779/4f4cf666-f468-475d-b847-7251a1c4a029)
## You can find data.json inside /src/main/resources/static/data.json to test the api/v1/execution-facts/upload endpoint
## For the liquibase to work properly the DB_USERNAME must have DDL Permissions and DML Permissions
## Project also contains documentation that you can check inside class files
## I decided not to use the parser I developed at task one as it is not the best solution for parsing a task that is present in api/v1/execution-facts/upload, as I honed that parser for statistics gathering purposes.


