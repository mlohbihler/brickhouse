package org.brickhouse.impl;

import org.apache.commons.dbcp2.BasicDataSource;

public class DataSourceBuilder {
    private final BasicDataSource ds;

    public DataSourceBuilder() {
        ds = new BasicDataSource();
    }

    public DataSourceBuilder driverClassName(String driverClassName) {
        ds.setDriverClassName(driverClassName);
        return this;
    }

    public DataSourceBuilder url(String url) {
        ds.setUrl(url);
        return this;
    }

    public DataSourceBuilder username(String username) {
        ds.setUsername(username);
        return this;
    }

    public DataSourceBuilder password(String password) {
        ds.setPassword(password);
        return this;
    }

    public BasicDataSource build() {
        return ds;
    }
}
