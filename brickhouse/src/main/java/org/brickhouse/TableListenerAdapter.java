package org.brickhouse;

import java.util.List;

import org.brickhouse.datatype.HMap;
import org.brickhouse.filter.Filter;

public class TableListenerAdapter implements TableListener {
    @Override
    public void insert(HMap record) {
        // no op
    }

    @Override
    public void batchInsert(List<HMap> records) {
        // no op
    }

    @Override
    public void update(HMap map, int count) {
        // no op
    }

    @Override
    public void deleteById(String id, int count) {
        // no op
    }

    @Override
    public void deleteAll(Filter filter, int count) {
        // no op
    }
}
