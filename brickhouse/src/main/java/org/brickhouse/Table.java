package org.brickhouse;

import java.io.Closeable;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Consumer;

import org.brickhouse.datatype.HMap;
import org.brickhouse.datatype.HReference;
import org.brickhouse.datatype.HRemove;
import org.brickhouse.datatype.HValue;
import org.brickhouse.filter.Filter;

public interface Table extends Closeable {
    HMap read(Filter filter);

    HMap read(Filter filter, boolean setDis);

    HMap readById(String id);

    HMap readById(String id, boolean setDis);

    HMap readById(HReference id);

    HMap readById(HReference id, boolean setDis);

    void readAll(Filter filter, Consumer<HMap> consumer);

    void readAll(Filter filter, Consumer<HMap> consumer, boolean setDis);

    List<HMap> readAll(Filter filter);

    List<HMap> readAll(Filter filter, boolean setDis);

    int count(Filter filter);

    void insert(HMap record);

    void batchInsert(List<HMap> records);

    int updateById(HMap map);

    int deleteById(String id);

    int deleteById(HReference id);

    int deleteAll(Filter filter);

    boolean include(HMap map, Filter filter);

    void addListener(TableListener listener);

    void removeListener(TableListener listener);

    default HMap clean(HMap map) {
        Iterator<Entry<String, HValue>> iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Entry<String, HValue> e = iter.next();
            if (e.getValue() instanceof HRemove)
                iter.remove();
        }
        return map;
    }
}
