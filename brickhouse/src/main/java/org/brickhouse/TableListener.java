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
