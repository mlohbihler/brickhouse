package org.brickhouse;

import org.brickhouse.impl.MysqlImpl;
import org.brickhouse.impl.PostgresqlImpl;

public class DatabaseFactory {
    public enum DatabaseType {
        mysql, postgresql
    }

    public static Database open(DatabaseType type, String host, String schema, String username, String password) {
        if (type == DatabaseType.mysql)
            return new MysqlImpl(host, schema, username, password);
        if (type == DatabaseType.postgresql)
            return new PostgresqlImpl(host, schema, username, password);

        throw new RuntimeException("Type: " + type + " not supported");
    }
}
