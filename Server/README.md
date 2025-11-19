# Database Setup Instructions

1. Install [SQLite](https://sqlite.org/download.html)
2. Run `sqlite3` from the command line
3. Type `.cd <directory>` with `<directory>` replaced with the directory that `initial_db.sql` is in
4. Type `.read initial_db.sql`
5. `.cd` to the same directory as the server jar
6. `.save bagplanner_db.db`

# Endpoints

All endpoints accept and return JSON data

A lot of functions take tokens. To get a token, use `Login`

## GET endpoints:
`GetUserState`: Takes a token and returns all info about the user associated with the token.
```
GET http://server.com/GetUserState with body:
{
    "token":"insert-token-here", 
}

Will return:
{
    "username":"person",
    "budgets":[
        {
            "name":"My budget #mybudget",
            "allocated_dollars":100,
            "allocated_cents":99,
            "used_dollars":0,
            "used_cents":0
        }
    ],
    "connections":["notperson"],
    "balanceDollars":4,
    "balanceCents":60
}
```

`Login`: Takes a username and password and if it's valid returns a token, otherwise returns a 403 FORBIDDEN
```
GET http://server.com/Login with body:
{
    "username": "theman",
    "password": "mylittlepassword"
}

Will return:
{
    "token": "a-long-string-of-letters"
}
```

## POST endpoints:

All POST endpoints return an 200 OK with an empty body on success

`AddConnection`: Creates a connection between two users. Takes a token and a username to connect with. Returns an empty body and a 200 OK on success
```
POST http://server.com/AddConnection with body:
{
    "token": "atoken",
    "username": "someone"
}
```

`CreateBudget`: Takes a token and initial allocated funds (`Dollars` and `Cents`) and creates a budget tracker owned by the user associated with the token. Returns an empty body and a 200 OK on success

```
POST http://server.com/CreateBudget with body:
{
    "token": "atoken",
    "name": "My budget #mybudget",
    "dollars": 100,
    "cents": 50
}

(This budget will have $100.50 of allocated funds)
```

`Logout`: Takes a token and deletes the token, effectively logging the user out
```
POST http://server.com/Logout with body:
{
    "token": "sgsdjsdoigjsdoinsdlk"
}
```

`PayUser`: Takes a token and recipient's username, and transfer's money from the token owner's account to the recipient's account
```
POST http://server.com/PayUser with body:
{
    "token": "dsgsdgsdg",
    "recipientUsername": "epiccoolguy",
    "dollars": 5,
    "cents": 50
}

(This will transfer $5.50 from dsgsdgsdg's account to epiccoolguy's account)
```

`RegisterUser`: Takes a username and password, and registers a new account. Returns a 403 FORBIDDEN if the username is already registered.
```
POST http://server.com/Register with body:
{
    "username": "dogluvr304",
    "password": "wrrrrrruffff"
}
```

`Transfer`: Takes a token, account number, and amount to transfer from that account (`Dollars` and `Cents`). However, since the app isn't actually hooked up to a banking system, in it's current state it will do no validation and essentially give free money
```
POST http://server.com/Transfer with body:
{
    "token": "sadgsadlds",
    "accountNumber": 1245678,
    "dollars": 1235,
    "cents": 65
}
```