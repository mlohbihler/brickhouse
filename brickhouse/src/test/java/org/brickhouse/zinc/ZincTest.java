package org.brickhouse.zinc;

import java.io.StringWriter;
import java.util.ArrayList;

import org.brickhouse.datatype.HBinary;
import org.brickhouse.datatype.HBoolean;
import org.brickhouse.datatype.HCoordinates;
import org.brickhouse.datatype.HDate;
import org.brickhouse.datatype.HDateTime;
import org.brickhouse.datatype.HGrid;
import org.brickhouse.datatype.HMap;
import org.brickhouse.datatype.HMarker;
import org.brickhouse.datatype.HNumber;
import org.brickhouse.datatype.HReference;
import org.brickhouse.datatype.HRemove;
import org.brickhouse.datatype.HTime;
import org.brickhouse.datatype.HTimeZone;
import org.brickhouse.datatype.HUri;
import org.junit.Assert;
import org.junit.Test;

import com.serotonin.util.CollectionUtils;

/**
 * @author Matthew
 */
//@RunWith(MockitoJUnitRunner.class)
public class ZincTest {
    @Test
    public void typeTest() throws Exception {
        HMap meta = new HMap() //
                .put("a") //
                .put("b", "B") //
                .put("c", new HReference("C", "Cdis")) //
                .put("d", new HCoordinates(1.23, 3.456)) //
                .put("d1", new HCoordinates(-1.23, 3.456)) //
                .put("d2", new HCoordinates(1.23, -3.456)) //
                .put("d3", new HCoordinates(-1.23, -3.456)) //
                .put("e", new HDate(2015, 2, 15)) //
                .put("f", new HDateTime(2015, 2, 15, 18, 2, 31, HTimeZone.forName("Toronto"), 0)) //
                .put("f1",
                        new HDateTime(new HDate(2015, 2, 15), new HTime(18, 2, 31, 200), HTimeZone.forName("Toronto"),
                                0)) //
                .put("f2", new HDateTime(new HDate(2015, 2, 15), new HTime(18, 2, 31, 230), HTimeZone.UTC)) //
                .put("f3", new HDateTime(new HDate(2015, 2, 15), new HTime(18, 2, 31, 234), HTimeZone.UTC, 300)) //
                .put("g", new HTime(18, 3, 31, 123)) //
                .put("i", new HUri("http://google.ca")) //
                .put("j", new HBinary("text/plain")) //
                .put("l", HBoolean.FALSE) //
                .put("m", new HNumber(3.14)) //
                .put("n", new HNumber(3.14, "dicts")) //
                .put("n1", HNumber.POS_INF) //
                .put("n2", HNumber.NEG_INF) //
                .put("n3", HNumber.NaN) //
                .put("o", "!bang") //
                .put("p", "") //
                .put("q", HRemove.VALUE) //
        //.put("r", new HList(new HString("r1"), new HString("r2"))) //
        ;

        HGrid grid = new HGrid(meta, CollectionUtils.toList(meta, meta));
        String out = gridToString(grid);
        //        System.out.println(out);
        HGrid in = stringToGrid(out);

        checkDict(in.getMeta());
        checkDict(in.getRows().get(0));
        checkDict(in.getRows().get(1));

        Assert.assertEquals(grid, in);
    }

    private void checkDict(HMap map) throws Exception {
        Assert.assertEquals(HMarker.VALUE, map.get("a"));
        Assert.assertEquals("B", map.getString("b"));

        Assert.assertEquals("C", map.getReference("c").getId());
        Assert.assertEquals("Cdis", map.getReference("c").getDis());

        Assert.assertEquals(1.23, map.getCoordinates("d").getLat(), 0);
        Assert.assertEquals(3.456, map.getCoordinates("d").getLng(), 0);
        Assert.assertEquals(-1.23, map.getCoordinates("d1").getLat(), 0);
        Assert.assertEquals(3.456, map.getCoordinates("d1").getLng(), 0);
        Assert.assertEquals(1.23, map.getCoordinates("d2").getLat(), 0);
        Assert.assertEquals(-3.456, map.getCoordinates("d2").getLng(), 0);
        Assert.assertEquals(-1.23, map.getCoordinates("d3").getLat(), 0);
        Assert.assertEquals(-3.456, map.getCoordinates("d3").getLng(), 0);

        Assert.assertEquals(2015, map.getDate("e").getYear());
        Assert.assertEquals(2, map.getDate("e").getMonth());
        Assert.assertEquals(15, map.getDate("e").getDay());

        checkDateTime(map.getDateTime("f"), 2015, 2, 15, 18, 2, 31, 0, "Toronto", 0);
        checkDateTime(map.getDateTime("f1"), 2015, 2, 15, 18, 2, 31, 200, "Toronto", 0);
        checkDateTime(map.getDateTime("f2"), 2015, 2, 15, 18, 2, 31, 230, "UTC", 0);
        checkDateTime(map.getDateTime("f3"), 2015, 2, 15, 18, 2, 31, 234, "UTC", 300);

        Assert.assertEquals(18, map.getTime("g").getHour());
        Assert.assertEquals(3, map.getTime("g").getMinute());
        Assert.assertEquals(31, map.getTime("g").getSecond());
        Assert.assertEquals(123, map.getTime("g").getMs());

        Assert.assertEquals("http://google.ca", map.getUri("i").getValue());
        Assert.assertEquals("text/plain", map.getBinary("j").getMime());
        Assert.assertEquals(false, map.getBoolean("l"));
        Assert.assertEquals(3.14, map.getDouble("m"), 0);

        Assert.assertEquals(3.14, map.getNumber("n").getValue(), 0);
        Assert.assertEquals("dicts", map.getNumber("n").getUnit());
        Assert.assertEquals(Double.POSITIVE_INFINITY, map.getNumber("n1").getValue(), 0);
        Assert.assertEquals(Double.NEGATIVE_INFINITY, map.getNumber("n2").getValue(), 0);
        Assert.assertTrue(Double.isNaN(map.getNumber("n3").getValue()));

        Assert.assertEquals("!bang", map.getString("o"));
        Assert.assertEquals("", map.getString("p"));
    }

    private void checkDateTime(HDateTime dt, int y, int m, int d, int h, int min, int s, int ms, String tz, int off) {
        Assert.assertEquals(y, dt.getDate().getYear());
        Assert.assertEquals(m, dt.getDate().getMonth());
        Assert.assertEquals(d, dt.getDate().getDay());
        Assert.assertEquals(h, dt.getTime().getHour());
        Assert.assertEquals(min, dt.getTime().getMinute());
        Assert.assertEquals(s, dt.getTime().getSecond());
        Assert.assertEquals(ms, dt.getTime().getMs());
        Assert.assertEquals(tz, dt.getTz().name);
        Assert.assertEquals(off, dt.getTzOffset());
    }

    @Test
    public void emptyGridTest() throws Exception {
        HGrid grid = new HGrid(new ArrayList<HMap>());
        String out = gridToString(grid);
        HGrid in = stringToGrid(out);
        Assert.assertEquals(grid, in);
    }

    @Test
    public void emptyMapsTest() throws Exception {
        HGrid grid = new HGrid(CollectionUtils.toList(new HMap(), new HMap(), new HMap()));
        String out = gridToString(grid);
        HGrid in = stringToGrid(out);
        Assert.assertEquals(grid, in);
    }

    private String gridToString(HGrid grid) {
        StringWriter out = new StringWriter();
        ZincWriter writer = new ZincWriter(out);
        writer.writeGrid(grid);
        writer.close();
        return out.toString();
    }

    private HGrid stringToGrid(String s) {
        return new ZincReader(s).readGrid();
    }
}
