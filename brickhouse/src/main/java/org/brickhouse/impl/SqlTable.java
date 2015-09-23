package org.brickhouse.impl;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.mutable.MutableObject;
import org.brickhouse.CancelReadException;
import org.brickhouse.Table;
import org.brickhouse.TableListener;
import org.brickhouse.datatype.HMap;
import org.brickhouse.datatype.HReference;
import org.brickhouse.datatype.HValue;
import org.brickhouse.filter.Filter;
import org.brickhouse.filter.Filter.Pather;
import org.brickhouse.json.JsonUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonReader;
import com.serotonin.json.JsonWriter;

public class SqlTable implements Table {
    private final List<TableListener> listeners = new CopyOnWriteArrayList<>();

    final JdbcTemplate jt;
    final String name;
    final boolean setDis;
    final boolean stats;

    // SQL
    final String select;
    final String selectId;
    final String insert;
    final String update;
    final String delete;
    final String disRead;
    final String statInsert;

    public SqlTable(JdbcTemplate jt, String name, boolean setDis, boolean stats) {
        this.jt = jt;
        this.name = name;
        this.setDis = setDis;
        this.stats = stats;

        select = "SELECT json FROM " + name;
        selectId = "SELECT json FROM " + name + " WHERE id=?";
        insert = "INSERT INTO " + name + " (id, dis, json) VALUES (?,?,?)";
        update = "UPDATE " + name + " SET dis=?, json=? WHERE id=?";
        delete = "DELETE FROM " + name + " WHERE id=?";
        disRead = "SELECT dis FROM " + name + " WHERE id=?";
        statInsert = "INSERT INTO " + name + DatabaseImpl.STATS_SUFFIX
                + " (query, rows, included, dis, nanos, ts) VALUES (?,?,?,?,?,?)";
    }

    @Override
    public HMap read(Filter filter) {
        return read(filter, setDis);
    }

    @Override
    public HMap read(final Filter filter, boolean setDis) {
        final MutableObject<HMap> o = new MutableObject<>();

        try {
            jt.query(select, new RowCallbackHandler() {
                @Override
                public void processRow(ResultSet rs) throws SQLException {
                    HMap map = toMap(rs.getString(1));
                    if (filter.include(map, pather)) {
                        o.setValue(map);
                        throw new CancelReadException();
                    }
                }
            });
        }
        catch (CancelReadException e) {
            // no op
        }

        if (setDis)
            return fillDii(o.getValue());
        return o.getValue();
    }

    @Override
    public HMap readById(String id) {
        return readById(id, setDis);
    }

    @Override
    public HMap readById(String id, boolean setDis) {
        try {
            HMap map = toMap(jt.queryForObject(selectId, String.class, id));
            if (setDis)
                return fillDii(map);
            return map;
        }
        catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public HMap readById(HReference id) {
        return readById(id.getId(), setDis);
    }

    @Override
    public HMap readById(HReference id, boolean setDis) {
        return readById(id.getId(), setDis);
    }

    @Override
    public void readAll(Filter filter, Consumer<HMap> consumer) {
        readAll(filter, consumer, setDis);
    }

    @Override
    public List<HMap> readAll(Filter filter) {
        return readAll(filter, setDis);
    }

    @Override
    public List<HMap> readAll(Filter filter, boolean setDis) {
        final List<HMap> list = new ArrayList<>();
        readAll(filter, row -> list.add(row), setDis);
        return list;
    }

    @Override
    public int count(Filter filter) {
        final MutableInt count = new MutableInt();
        readAll(filter, row -> count.increment(), false);
        return count.intValue();
    }

    @Override
    public void readAll(final Filter filter, final Consumer<HMap> consumer, boolean fillDii) {
        final Map<String, String> disCache = fillDii ? new HashMap<>() : null;

        long start = System.nanoTime();
        final MutableInt count = new MutableInt();
        final MutableInt included = new MutableInt();

        try {
            jt.query(select, new RowCallbackHandler() {
                @Override
                public void processRow(ResultSet rs) throws SQLException {
                    HMap map = toMap(rs.getString(1));
                    count.increment();
                    if (filter.include(map, pather)) {
                        fillDii(map, disCache);
                        consumer.accept(map);
                        included.increment();
                    }
                }
            });
        }
        catch (CancelReadException e) {
            // no op
        }

        if (stats)
            saveStats(filter.toString(), count.intValue(), included.intValue(), fillDii, System.nanoTime() - start,
                    System.currentTimeMillis());
    }

    @Override
    public void insert(HMap map) {
        HReference id = map.id();
        if (id == null)
            throw new RuntimeException("id is required");

        clean(map);

        jt.update(insert, id.getId(), map.disOrNull(), toString(map));

        for (TableListener l : listeners)
            l.insert(map);
    }

    @Override
    public void batchInsert(List<HMap> rows) {
        jt.batchUpdate(insert, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                HMap row = rows.get(i);
                HReference id = row.id();
                if (id == null)
                    throw new RuntimeException("id is required");

                clean(row);

                ps.setString(1, id.getId());
                ps.setString(2, row.disOrNull());
                ps.setString(3, SqlTable.toString(row));
            }

            @Override
            public int getBatchSize() {
                return rows.size();
            }
        });

        for (TableListener l : listeners)
            l.batchInsert(rows);
    }

    @Override
    public int updateById(HMap map) {
        HReference id = map.id();
        if (id == null)
            throw new RuntimeException("id is required");
        String dis = map.disOrNull();

        clean(map);

        int count = jt.update(update, dis, toString(map), id.getId());

        for (TableListener l : listeners)
            l.update(map, count);

        return count;
    }

    @Override
    public int deleteById(String id) {
        int count = jt.update(delete, id);

        for (TableListener l : listeners)
            l.deleteById(id, count);

        return count;
    }

    @Override
    public int deleteById(HReference id) {
        return deleteById(id.getId());
    }

    @Override
    public int deleteAll(final Filter filter) {
        final MutableInt count = new MutableInt();

        jt.query(select, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                HMap map = toMap(rs.getString(1));
                if (filter.include(map, pather))
                    count.add(jt.update(delete, map.id().getId()));
            }
        });

        for (TableListener l : listeners)
            l.deleteAll(filter, count.intValue());

        return count.intValue();
    }

    @Override
    public boolean include(HMap map, Filter filter) {
        return filter.include(map, pather);
    }

    @Override
    public void close() {
        // no op
    }

    @Override
    public void addListener(TableListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(TableListener listener) {
        listeners.remove(listener);
    }

    //
    // Private(ish)
    //
    Pather pather = new Pather() {
        @Override
        public HMap find(String ref) {
            try {
                return toMap(jt.queryForObject(selectId, String.class, ref));
            }
            catch (EmptyResultDataAccessException e) {
                return null;
            }
        }
    };

    static HMap toMap(String s) {
        JsonReader reader = new JsonReader(JsonUtils.dbCtx(), s);
        try {
            return reader.read(HMap.class);
        }
        catch (JsonException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    static String toString(HMap map) {
        StringWriter out = new StringWriter();
        try {
            new JsonWriter(JsonUtils.dbCtx(), out).writeObject(map);
        }
        catch (JsonException | IOException e) {
            throw new RuntimeException(e);
        }
        return out.toString();
    }

    HMap fillDii(HMap map) {
        if (map != null) {
            Map<String, String> cache = new HashMap<>();
            fillDii(map, cache);
        }
        return map;
    }

    static final String NULL_STRING = "__null__";

    void fillDii(HMap map, Map<String, String> cache) {
        if (cache == null)
            return;

        // Add the dis of this map to the cache.
        cache.put(map.id().getId(), map.disOrNull());

        HMap refs = null;
        for (Entry<String, HValue> e : map.entrySet()) {
            /* Don't fill id fields. */
            if (e.getValue() instanceof HReference && !"id".equals(e.getKey())) {
                HReference r = (HReference) e.getValue();

                /* Check the cache. */
                String dis = cache.get(r.getId());
                if (dis == null) {
                    /* Do a db lookup. */
                    dis = getDis(r.getId());
                    if (dis == null)
                        dis = NULL_STRING;
                    cache.put(r.getId(), dis);
                }

                if (dis != NULL_STRING) {
                    if (refs == null)
                        refs = new HMap();
                    refs.put(e.getKey(), new HReference(r.getId(), dis));
                }
            }
        }
        if (refs != null)
            map.merge(refs);
    }

    private String getDis(String id) {
        try {
            return jt.queryForObject(disRead, String.class, id);
        }
        catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    void saveStats(String filter, int count, int included, boolean fillDii, long elapsed, long ts) {
        jt.update(statInsert, filter, count, included, fillDii ? "Y" : "N", elapsed, ts);
    }
}
