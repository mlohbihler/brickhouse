// Copyright (c) 2015, StackHub.org
package org.brickhouse.json;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

import org.brickhouse.datatype.HMap;
import org.brickhouse.datatype.HValue;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonReader;
import com.serotonin.json.JsonWriter;
import com.serotonin.json.ObjectWriter;
import com.serotonin.json.convert.ObjectJsonWriter;
import com.serotonin.json.spi.ClassConverter;
import com.serotonin.json.type.JsonObject;
import com.serotonin.json.type.JsonTypeWriter;
import com.serotonin.json.type.JsonValue;

public class HMapConverter implements ClassConverter {
    @Override
    public JsonValue jsonWrite(JsonTypeWriter writer, Object value) throws JsonException {
        throw new RuntimeException();
    }

    @Override
    public void jsonWrite(JsonWriter writer, Object value) throws IOException, JsonException {
        HMap map = (HMap) value;
        ObjectWriter objectWriter = new ObjectJsonWriter(writer);
        for (Map.Entry<String, HValue> e : map.entrySet())
            objectWriter.writeEntry(e.getKey(), e.getValue());
        objectWriter.finish();
    }

    @Override
    public Object jsonRead(JsonReader reader, JsonValue jsonValue, Type type) throws JsonException {
        JsonObject o = jsonValue.toJsonObject();

        HMap map = new HMap();
        for (Map.Entry<String, JsonValue> e : o.entrySet()) {
            HValue val = reader.read(HValue.class, e.getValue());
            map.put(e.getKey(), val);
        }

        return map;
    }

    @Override
    public void jsonRead(JsonReader reader, JsonValue jsonValue, Object obj, Type type) throws JsonException {
        throw new RuntimeException();
    }
}
