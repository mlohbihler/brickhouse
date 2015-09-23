package test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.brickhouse.Database;
import org.brickhouse.DatabaseFactory;
import org.brickhouse.DatabaseFactory.DatabaseType;
import org.brickhouse.Table;
import org.brickhouse.datatype.HMap;
import org.brickhouse.filter.Filter;
import org.brickhouse.impl.DataSourceBuilder;
import org.brickhouse.json.JsonUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;

import com.serotonin.json.JsonWriter;

public class Test {
    public static void main(String[] args) throws Exception {
        //        main1();
        //        main2();
        test3();
    }

    static void test3() throws Exception {
        Map<String, String> map = new ConcurrentHashMap<>();
        map.put("a", "A");
        map.put("b", "B");
        map.put("c", "C");
        map.put("d", "D");
        map.put("e", "E");
        map.put("f", "F");
        map.put("g", "G");
        map.put("h", "H");
        map.put("i", "I");
        map.put("j", "J");
        map.put("k", "K");
        map.put("l", "L");
        map.put("m", "M");
        map.put("n", "N");
        map.put("o", "O");
        map.put("p", "P");

        for (Entry<String, String> e : map.entrySet()) {
            if (e.getKey().equals("i") || e.getKey().equals("j"))
                map.remove(e.getKey());
        }

        System.out.println(map);
    }

    public static void main1() throws Exception {
        Database db = DatabaseFactory.open(DatabaseType.mysql, "brooks", "brickhouse", "ml", "ml");
        //        Database db = DatabaseFactory.open(DatabaseType.postgresql, "brooks", "treehouse_dev", "treehouse_user",
        //                "treehouse_user");
        Table table = db.getTable("test", true);

        //        table.insert(new HMap().put("test").put("a", "b").put("c", 314).put("id", new HReference())
        //                .put("dis", "test record 2").put("friend", new HReference("24223b64-d2a3-4f0d-ae7d-b4ee18fb5367")));

        //        System.out.println(table.readAll(HFilter.has("id")));

        //        System.out.println(table.readById(new HReference("24223b64-d2a3-4f0d-ae7d-b4ee18fb5367")));
        //        System.out.println(table.readById("24223b64-d2a3-4f0d-ae7d-b4ee18fb5367"));

        //        HMap map = table.readById("24223b64-d2a3-4f0d-ae7d-b4ee18fb5367");
        //        map.put("zxcv").remove("a").put("q", new HDateTime(System.currentTimeMillis()));
        //        table.updateById(map);
        //        System.out.println(table.readById("24223b64-d2a3-4f0d-ae7d-b4ee18fb5367"));

        //        HMap map = table.readById("85cf1f69-f4ce-4a75-9627-5100c0eb8b97");
        //        System.out.println(JsonWriter.writeToString(JsonUtils.ctx(), map));

        //        deleteById(table);
        //        readAll(table);
        filter(table, "id==@24223b64-d2a3-4f0d-ae7d-b4ee18fb5367");
    }

    static void filter(Table table, String filterStr) throws Exception {
        List<HMap> list = table.readAll(Filter.parse(filterStr));
        System.out.println(JsonWriter.writeToString(JsonUtils.ctx(), list));
    }

    static void deleteById(Table table) throws Exception {
        table.deleteById("18a63f15-ddd2-460e-8599-fea0c5e68071");
    }

    static void readAll(Table table) throws Exception {
        Filter filter = Filter.parse("*");
        List<HMap> list = table.readAll(filter);
        System.out.println(JsonWriter.writeToString(JsonUtils.ctx(), list));
    }

    static void main2() throws Exception {
        DataSource ds = new DataSourceBuilder() //
                .driverClassName("com.mysql.jdbc.Driver") //
                .url("jdbc:mysql://brooks/brickhouse") //
                .username("ml") //
                .password("ml") //
                .build();
        JdbcTemplate jt = new JdbcTemplate(ds);

        boolean found = jt.execute(new ConnectionCallback<Boolean>() {
            @Override
            public Boolean doInConnection(Connection con) throws SQLException, DataAccessException {
                ResultSet rs = con.getMetaData().getTables(null, null, "testy", null);
                return rs.next();
            }
        });

        System.out.println(found);
    }
}
