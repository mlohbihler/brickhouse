# brickhouse
Implementation of tag database on top of SQL

Brickhouse is a tag database implementation that relies upon a SQL database for its underlying persistence. Currently MySQL and PostgreSQL are supported. There is also an in-memory implementation that wraps a SQL impl and provides much better performance (and uses write-behind for persistence).

This work is based upon the Haystack data model, and implements all Haystack data types, JSON and Zinc encoding, and query filters with additional operators (like, ilike, and negation).

# Usage
    Database db = DatabaseFactory.open(DatabaseType.postgresql, dbHost, dbSchema, dbUser, dbPass);
    Table table = new MemoryTable(db, "common", true, true, false);

This creates an in-memory table that will be backed by a PostgreSQL database. A tag database has a single table upon which all queries are run.
