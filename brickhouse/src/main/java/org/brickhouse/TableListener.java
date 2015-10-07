/* 
 * Copyright (c) 2015, Matthew Lohbihler
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.brickhouse;

import java.util.List;

import org.brickhouse.datatype.HMap;
import org.brickhouse.filter.Filter;

public interface TableListener {
    void insert(HMap record);

    void batchInsert(List<HMap> records);

    void update(HMap map, int count);

    void deleteById(String id, int count);

    void deleteAll(Filter filter, int count);
}
