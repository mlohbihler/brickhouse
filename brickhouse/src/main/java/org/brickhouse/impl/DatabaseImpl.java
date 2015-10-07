/* 
 * Copyright (c) 2015, Matthew Lohbihler
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.brickhouse.impl;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp2.BasicDataSource;
import org.brickhouse.Database;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;

public abstract class DatabaseImpl implements Database {
    static final String STATS_SUFFIX = "_stats";

    private final BasicDataSource ds;
    protected final JdbcTemplate jt;

    public DatabaseImpl(BasicDataSource ds) {
        this.ds = ds;
        jt = new JdbcTemplate(ds);
    }

    @Override
    public SqlTable getTable(String name, boolean create) {
        return getTable(name, create, true, false);
    }

    @Override
    public SqlTable getTable(String name, boolean create, boolean setDis, boolean stats) {
        if (!tableExists(name)) {
            if (create)
                createTable(name);
            else
                return null;
        }

        if (stats && !tableExists(name + STATS_SUFFIX))
            createStatsTable(name);

        return new SqlTable(jt, name, setDis, stats);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    @Override
    public JdbcTemplate getJdbcTemplate() {
        return jt;
    }

    @Override
    public void close() throws IOException {
        try {
            ds.close();
        }
        catch (SQLException e) {
            throw new IOException(e);
        }
    }

    protected boolean tableExists(final String name) {
        return jt.execute(new ConnectionCallback<Boolean>() {
            @Override
            public Boolean doInConnection(Connection con) throws SQLException, DataAccessException {
                return con.getMetaData().getTables(null, null, name.toLowerCase(), null).next();
            }
        });
    }

    abstract protected void createTable(String name);

    abstract protected void createStatsTable(String name);
}
