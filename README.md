# brickhouse
Implementation of tag database on top of SQL

Brickhouse is a tag database implementation that relies upon a SQL database for its underlying persistence. 
Currently MySQL and PostgreSQL are supported. There is also an in-memory implementation that wraps a SQL impl 
and provides much better performance (and uses write-behind for persistence).

This work is based upon the Haystack data model, and implements all Haystack data types, JSON and Zinc encoding, 
and query filters with additional operators (like, ilike, and negation).
