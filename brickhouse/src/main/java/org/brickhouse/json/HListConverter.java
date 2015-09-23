// Copyright (c) 2015, StackHub.org
package org.brickhouse.json;

import java.io.IOException;
import java.lang.reflect.Type;

import org.brickhouse.datatype.HList;
import org.brickhouse.datatype.HValue;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonReader;
import com.serotonin.json.JsonWriter;
import com.serotonin.json.spi.ClassConverter;
import com.serotonin.json.type.JsonArray;
import com.serotonin.json.type.JsonTypeWriter;
import com.serotonin.json.type.JsonValue;

public class HListConverter implements ClassConverter {
    @Override
    public JsonValue jsonWrite(JsonTypeWriter writer, Object value) throws JsonException {
        throw new RuntimeException();
    }

    @Override
    public void jsonWrite(JsonWriter writer, Object value) throws IOException, JsonException {
        HList list = (HList) value;
        boolean first = true;

        writer.append('[');
        writer.increaseIndent();

        for (HValue hv : list.getList()) {
            if (first)
                first = false;
            else
                writer.append(',');
            writer.indent();
            writer.writeObject(hv);
        }

        writer.decreaseIndent();
        writer.indent();
        writer.append(']');
    }

    @Override
    public Object jsonRead(JsonReader reader, JsonValue jsonValue, Type type) throws JsonException {
        JsonArray a = jsonValue.toJsonArray();

        HList list = new HList();
        for (JsonValue e : a) {
            HValue val = reader.read(HValue.class, e);
            list.add(val);
        }

        return list;
    }

    @Override
    public void jsonRead(JsonReader reader, JsonValue jsonValue, Object obj, Type type) throws JsonException {
        throw new RuntimeException();
    }
}
