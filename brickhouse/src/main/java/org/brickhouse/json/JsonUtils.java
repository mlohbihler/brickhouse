package org.brickhouse.json;

import org.brickhouse.datatype.HBinary;
import org.brickhouse.datatype.HBoolean;
import org.brickhouse.datatype.HCoordinates;
import org.brickhouse.datatype.HDate;
import org.brickhouse.datatype.HDateTime;
import org.brickhouse.datatype.HGrid;
import org.brickhouse.datatype.HList;
import org.brickhouse.datatype.HMap;
import org.brickhouse.datatype.HMarker;
import org.brickhouse.datatype.HNumber;
import org.brickhouse.datatype.HReference;
import org.brickhouse.datatype.HRemove;
import org.brickhouse.datatype.HString;
import org.brickhouse.datatype.HTime;
import org.brickhouse.datatype.HUri;
import org.brickhouse.datatype.HValue;

import com.serotonin.json.JsonContext;

public class JsonUtils {
    private static JsonContext jctx;
    private static JsonContext dbCtx;

    public static JsonContext ctx() {
        if (jctx == null) {
            synchronized (JsonUtils.class) {
                if (jctx == null) {
                    JsonContext c = basicCtx();
                    c.addConverter(new HReferenceConverter(), HReference.class);
                    jctx = c;
                }
            }
        }
        return jctx;
    }

    public static JsonContext dbCtx() {
        if (dbCtx == null) {
            synchronized (JsonUtils.class) {
                if (dbCtx == null) {
                    JsonContext c = basicCtx();
                    // Use the reference converter for the database, which doesn't read or write the dis.
                    c.addConverter(new HReferenceDBConverter(), HReference.class);
                    dbCtx = c;
                }
            }
        }
        return dbCtx;
    }

    private static JsonContext basicCtx() {
        JsonContext c = new JsonContext();
        c.setEscapeForwardSlash(false);
        c.addConverter(new HBinaryConverter(), HBinary.class);
        c.addConverter(new HBooleanConverter(), HBoolean.class);
        c.addConverter(new HCoordinatesConverter(), HCoordinates.class);
        c.addConverter(new HDateConverter(), HDate.class);
        c.addConverter(new HDateTimeConverter(), HDateTime.class);
        c.addConverter(new HGridConverter(), HGrid.class);
        c.addConverter(new HListConverter(), HList.class);
        c.addConverter(new HMapConverter(), HMap.class);
        c.addConverter(new HMarkerConverter(), HMarker.class);
        c.addConverter(new HNumberConverter(), HNumber.class);
        c.addConverter(new HRemoveConverter(), HRemove.class);
        c.addConverter(new HStringConverter(), HString.class);
        c.addConverter(new HTimeConverter(), HTime.class);
        c.addConverter(new HUriConverter(), HUri.class);
        c.addResolver(new HValueResolver(), HValue.class);
        return c;
    }
}
