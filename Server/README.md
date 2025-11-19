# Database Setup Instructions

1. Install [SQLite](https://sqlite.org/download.html)
2. Run `sqlite3` from the command line
3. Type `.cd <directory>` with `<directory>` replaced with the directory that `initial_db.sql` is in
4. Type `.read initial_db.sql`
5. `.cd` to the same directory as the server jar
6. `.save bagplanner_db.db`