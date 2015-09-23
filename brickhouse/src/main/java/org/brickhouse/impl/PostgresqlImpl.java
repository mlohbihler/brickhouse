package org.brickhouse.impl;

public class PostgresqlImpl extends DatabaseImpl {
    public PostgresqlImpl(String host, String schema, String username, String password) {
        super(new DataSourceBuilder() //
                .driverClassName("org.postgresql.Driver") //
                .url("jdbc:postgresql://" + host + "/" + schema) //
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
                + " (query TEXT, rows INTEGER, included INTEGER, dis CHAR(1), nanos BIGINT, ts BIGINT)");
    }
}
