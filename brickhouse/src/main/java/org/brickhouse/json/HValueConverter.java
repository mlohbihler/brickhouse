package org.brickhouse.json;

import java.io.IOException;
import java.lang.reflect.Type;

import org.brickhouse.datatype.HValue;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonReader;
import com.serotonin.json.JsonWriter;
import com.serotonin.json.spi.ClassConverter;
import com.serotonin.json.type.JsonTypeWriter;
import com.serotonin.json.type.JsonValue;

abstract public class HValueConverter implements ClassConverter {
    public static final char ESCAPE = ':';

    @Override
    public JsonValue jsonWrite(JsonTypeWriter writer, Object value) throws JsonException {
        throw new RuntimeException();
    }

    @Override
    public void jsonWrite(JsonWriter writer, Object value) throws IOException, JsonException {
        StringBuilder sb = new StringBuilder().append(getTypeCode()).append(ESCAPE).append(toString(value));
        writer.quote(sb.toString());
    }

    abstract protected char getTypeCode();

    abstract protected String toString(Object value);

    @Override
    public Object jsonRead(JsonReader reader, JsonValue jsonValue, Type type) throws JsonException {
        return fromString(jsonValue.toString().substring(2));
    }

    abstract protected HValue fromString(String value) throws JsonException;

    @Override
    public void jsonRead(JsonReader reader, JsonValue jsonValue, Object obj, Type type) throws JsonException {
        throw new RuntimeException();
    }
}
