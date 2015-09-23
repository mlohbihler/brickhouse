package test;

import static org.brickhouse.filter.Filter.has;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import org.brickhouse.Database;
import org.brickhouse.DatabaseFactory;
import org.brickhouse.DatabaseFactory.DatabaseType;
import org.brickhouse.Table;
import org.brickhouse.datatype.HMap;
import org.brickhouse.datatype.HNumber;
import org.brickhouse.datatype.HReference;
import org.brickhouse.datatype.HValue;
import org.brickhouse.filter.Filter;
import org.brickhouse.impl.MemoryTable;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import com.serotonin.db.pair.StringStringPair;
import com.serotonin.util.ExecutionTimerNano;

public class Perf {
    static final Random RANDOM = new Random();

    public static void main(String[] args) throws Exception {
        new Perf();
    }

    //    CREATE TABLE perfTagIndex (
    //            name VARCHAR(64) NOT NULL,
    //            id VARCHAR(50) NOT NULL,
    //            PRIMARY KEY (name, id)
    //          );

    static final String[] dii = { "doc", "dopey", "bashful", "grumpy", "sneezy", "sleepy", "happy" };

    static final String[] markers = { "alpha", "bravo", "charlie", "delta", "echo", "foxtrot", "golf", "hotel",
            "india", "juliet", "kilo", "lima", "mike", "november", "oscar", "papa", "quebec", "romeo", "sierra",
            "tango", "uniform", "victor", "whiskey", "xray", "yankee", "zulu" };

    JdbcTemplate jt;
    Table table;
    ExecutionTimerNano timer = new ExecutionTimerNano();

    public Perf() throws Exception {
        Database db = null;
        try {
            db = DatabaseFactory.open(DatabaseType.postgresql, "brooks", "treehouse_dev", "treehouse_user",
                    "treehouse_user");
            jt = db.getJdbcTemplate();

            //        table = db.getTable("perf", true, true, true);
            table = new MemoryTable(db, "perf", true, true, true);

            generate(100);
            //        generate(10000);
            //        generate(20000);
            //        generate(30000);
            //        generate(40000);

            read();

            //        indexUpdate();

            //        readJoin();

            //        counts();

            //        refTest();

            System.out.println(timer);
            System.out.println(table);
        }
        finally {
            table.close();
            db.close();
        }
        System.out.println("Perf done");
    }

    void generate(int rows) {
        // Get some references
        timer.start();
        final HReference[] refs = new HReference[100];
        table.readAll(Filter.ALL, row -> refs[RANDOM.nextInt(refs.length)] = row.id());
        timer.mark("refs");

        List<HMap> batch = new ArrayList<>();
        for (int i = 0; i < rows; i++) {
            HMap row = new HMap();
            HReference id = new HReference();

            row.put("id", id);
            timer.mark("id");

            int markerCount = RANDOM.nextInt(10);
            for (int j = 0; j < markerCount; j++) {
                int r = (int) StrictMath.sqrt(RANDOM.nextDouble() * markers.length * markers.length);
                if (r >= markers.length)
                    r = markers.length - 1;
                row.put(markers[r]);
            }
            timer.mark("markers");

            row.put("dis", dii[RANDOM.nextInt(dii.length)]);
            timer.mark("dis");

            row.put("number", new HNumber(RANDOM.nextDouble() * 100));
            timer.mark("number");

            HReference ref = refs[RANDOM.nextInt(refs.length)];
            if (ref != null)
                row.put("rowRef", ref);
            timer.mark("rowRef");

            batch.add(row);
            timer.mark("add");

            refs[RANDOM.nextInt(refs.length)] = id;
            timer.mark("newRef");
        }

        table.batchInsert(batch);
        timer.mark("insert");
    }

    void read() {
        timer.start();
        table.readAll(Filter.ALL, row -> {
            timer.mark("read1.row");
        }, true);
        timer.mark("read1");

        timer.start();
        table.readAll(has(markers[0]), row -> {
            timer.mark("read2.row");
        }, true);
        timer.mark("read2");

        timer.start();
        table.readAll(has(markers[1]).and(has(markers[2])), row -> {
            timer.mark("read3.row");
        }, true);
        timer.mark("read3");

        timer.start();
        table.readAll(has(markers[3]).and(Filter.gt("number", new HNumber(50))), row -> {
            timer.mark("read4.row");
        }, true);
        timer.mark("read4");

        timer.start();
        table.readAll(has("rowRef->" + markers[4]), row -> {
            timer.mark("read5.row");
        }, true);
        timer.mark("read5");
    }

    void indexUpdate() {
        timer.start();
        table.readAll(Filter.ALL, row -> {
            final List<StringStringPair> batch = new ArrayList<>();
            String id = row.id().getId();
            for (Entry<String, HValue> e : row.entrySet()) {
                if (!"id".equals(e.getKey()))
                    batch.add(new StringStringPair(e.getKey(), id));
            }

            jt.batchUpdate("INSERT INTO perfTagIndex (name, id) VALUES (?,?)", new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setString(1, batch.get(i).getKey());
                    ps.setString(2, batch.get(i).getValue());
                }

                @Override
                public int getBatchSize() {
                    return batch.size();
                }
            });
        }, false);
        timer.mark("indexUpdate");
    }

    void readJoin() {
        timer.start();
        table.readAll(has(markers[25]), row -> timer.mark("read2.row"), false);
        timer.mark("read2");
    }

    void counts() {
        //        System.out.println(table.count(Filter.ALL));
        System.out.println(table.count(has(markers[0])));
        System.out.println(table.count(has(markers[1])));
        System.out.println(table.count(has(markers[2])));
        System.out.println(table.count(has(markers[3])));
        System.out.println(table.count(has(markers[4])));
        System.out.println(table.count(has(markers[5])));
        System.out.println(table.count(has(markers[6])));
        System.out.println(table.count(has(markers[7])));
        System.out.println(table.count(has(markers[8])));
        System.out.println(table.count(has(markers[9])));
        System.out.println(table.count(has(markers[10])));
        System.out.println(table.count(has(markers[11])));
        System.out.println(table.count(has(markers[12])));
        System.out.println(table.count(has(markers[13])));
        System.out.println(table.count(has(markers[14])));
        System.out.println(table.count(has(markers[15])));
        System.out.println(table.count(has(markers[16])));
        System.out.println(table.count(has(markers[17])));
        System.out.println(table.count(has(markers[18])));
        System.out.println(table.count(has(markers[19])));
        System.out.println(table.count(has(markers[20])));
        System.out.println(table.count(has(markers[21])));
        System.out.println(table.count(has(markers[22])));
        System.out.println(table.count(has(markers[23])));
        System.out.println(table.count(has(markers[24])));
        System.out.println(table.count(has(markers[25])));
    }

    //    void refTest() {
    //        //        String name = "alpha";
    //        //        String name = "kilo";
    //        String name = "zulu";
    //
    //        timer.start();
    //        List<HMap> rows = table.readAll(has(name), false);
    //        System.out.println("Rows: " + rows.size());
    //        timer.mark("read1");
    //
    //        rows = table.readAll(has(name), false);
    //        timer.mark("read2");
    //
    //        StringBuilder sb = new StringBuilder();
    //        final Map<String, List<HReference>> lu = new HashMap<>();
    //        for (HMap row : rows) {
    //            for (Entry<String, HValue> e : row.entrySet()) {
    //                if (e.getValue() instanceof HReference && !"id".equals(e.getKey())) {
    //                    HReference ref = (HReference) e.getValue();
    //                    List<HReference> list = lu.get(ref.getId());
    //                    if (list == null) {
    //                        list = new ArrayList<>();
    //                        lu.put(ref.getId(), list);
    //
    //                        if (sb.length() > 0)
    //                            sb.append(',');
    //                        sb.append('\'').append(e.getValue()).append('\'');
    //                    }
    //                    list.add(ref);
    //                }
    //            }
    //        }
    //        timer.mark("where");
    //
    //        if (!lu.isEmpty()) {
    //            System.out.println("IDs: " + lu.size());
    //            jt.query("select id, dis from perf where id in (" + sb.toString() + ")", new RowCallbackHandler() {
    //                @Override
    //                public void processRow(ResultSet rs) throws SQLException {
    //                    for (HReference ref : lu.get(rs.getString(1)))
    //                        ref.setDis(rs.getString(2));
    //                }
    //            });
    //        }
    //        timer.mark("fill");
    //
    //        List<HMap> rows2 = table.readAll(has(name), true);
    //        timer.mark("readDis");
    //    }
}
