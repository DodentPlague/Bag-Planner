PRAGMA foreign_keys=ON;
BEGIN TRANSACTION;

CREATE TABLE Users(
    id INTEGER PRIMARY KEY ASC, 
    username TEXT, 
    password BLOB, 
    salt BLOB, 
    balance_dollar INTEGER,
    balance_cent INTEGER 
);

CREATE TABLE Budgets(
    user_id INTEGER,
    name TEXT,
    allocated_funds_dollars INTEGER,
    allocated_funds_cents INTEGER,
    funds_used_dollars INTEGER,
    funds_used_cents INTEGER,

    FOREIGN KEY(user_id) REFERENCES User(id)
);

CREATE TABLE Connections(
    id1 INTEGER,
    id2 INTEGER,

    FOREIGN KEY(id1) REFERENCES User(id),
    FOREIGN KEY(id2) REFERENCES User(id)
);

COMMIT;