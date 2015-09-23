package org.brickhouse;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.SQLException;

import org.brickhouse.impl.SqlTable;
import org.springframework.jdbc.core.JdbcTemplate;

public interface Database extends Closeable {
    /**
     * Convenience method for getTable(String, boolean, boolean), defaulting the "setDis" value to true.
     * 
     * @param name
     *            table name
     * @param create
     *            whether to create the table if it doesn't exist
     * @return the table, or null if it does not exist.
     */
    SqlTable getTable(String name, boolean create);

    /**
     * Gets a proxy of the database table of the given name.
     * 
     * @param name
     *            the table name
     * @param create
     *            whether to create the table if it doesn't exist.
     * @param setDis
     *            whether to set the 'dis' values of references in maps returned from read requests. This can cause high
     *            overhead, and so should only be used if required. Note that this only sets the default value for the
     *            table instance. Read requests allow the passing of an override value so that this can be used on a
     *            per-request basis.
     * @param setDis
     *            whether to track the stats of readAll queries
     * @return the table proxy, or null of it does not exist (and create was false).
     */
    SqlTable getTable(String name, boolean create, boolean setDis, boolean stats);

    Connection getConnection() throws SQLException;

    JdbcTemplate getJdbcTemplate();
}
