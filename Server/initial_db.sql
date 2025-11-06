PRAGMA foreign_keys=OFF;
BEGIN TRANSACTION;
CREATE TABLE Accounts(id INTEGER PRIMARY KEY ASC, username VARCHAR(64) NOT NULL);
INSERT INTO Accounts VALUES(1,'user1');
INSERT INTO Accounts VALUES(2,'thesillyguy');
INSERT INTO Accounts VALUES(3,'mrrrrrrowwwwww');
COMMIT;