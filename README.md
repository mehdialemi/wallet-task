# simple wallet
This is a wallet app to manage user balance, transaction, history, and scheduled transaction.

## design choice
- There is one wallet service which accepts wallet api REST requests including create, deposit, withdraw, history of an already created user.
- Wallet uses sql database (mysql) to store transaction and wallet data.
- Users should first registered throw /register api and after that login by /login api.
- The apis, register and login, don't require the authorization header to process.
- Set jwt token in Authorization header as "Bearer ${token}".
- Any registered users can call the api specified at swagger url.
- To call wallet api by swagger ui, you should set jwt token at authorization section.

## open api
The swagger url for api definition and calling those, after starting the program, go to the following link:
http://localhost:8080/swagger-ui.html

## docker
To immediately start an instance of program go to the root directory of the project and run the following command.
```
mvn clean package
docker compose up --build
```

