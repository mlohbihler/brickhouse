/* 
 * Copyright (c) 2015, Matthew Lohbihler
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.brickhouse.impl;

public class MysqlImpl extends DatabaseImpl {
    public MysqlImpl(String host, String schema, String username, String password) {
        super(new DataSourceBuilder() //
                .driverClassName("com.mysql.jdbc.Driver") //
                .url("jdbc:mysql://" + host + "/" + schema) //
                .username(username) //
                .password(password) //
                .build());
    }

    @Override
    protected void createTable(String name) {
        jt.execute("CREATE TABLE " + name + " (id VARCHAR(50) NOT NULL, dis TEXT, json TEXT, PRIMARY KEY (id))");
    }

    @Override
    protected void createStatsTable(String name) {
        jt.execute("CREATE TABLE " + name + STATS_SUFFIX
                + " (query TEXT, rows INT, included INT, dis CHAR(1), nanos BIGINT, ts BIGINT)");
    }
}
