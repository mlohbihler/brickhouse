/* 
 * Copyright (c) 2015, Matthew Lohbihler
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
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
