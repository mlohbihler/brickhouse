/* 
 * Copyright (c) 2015, Matthew Lohbihler
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.brickhouse.json;

import java.io.IOException;
import java.lang.reflect.Type;

import org.brickhouse.datatype.HString;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonReader;
import com.serotonin.json.JsonWriter;
import com.serotonin.json.spi.ClassConverter;
import com.serotonin.json.type.JsonTypeWriter;
import com.serotonin.json.type.JsonValue;

public class HStringConverter implements ClassConverter {
    public static final char CODE = 's';

    @Override
    public JsonValue jsonWrite(JsonTypeWriter writer, Object value) throws JsonException {
        throw new RuntimeException();
    }

    @Override
    public void jsonWrite(JsonWriter writer, Object value) throws IOException, JsonException {
        String s = value.toString();
        if (s.length() > 1 && s.charAt(1) == HValueConverter.ESCAPE)
            writer.quote(CODE + HValueConverter.ESCAPE + value.toString());
        else
            writer.quote(value.toString());
    }

    @Override
    public Object jsonRead(JsonReader reader, JsonValue jsonValue, Type type) throws JsonException {
        String s = jsonValue.toString();
        if (s.length() > 1 && s.startsWith(CODE + "" + HValueConverter.ESCAPE))
            return new HString(s.substring(2));
        return new HString(s);
    }

    @Override
    public void jsonRead(JsonReader reader, JsonValue jsonValue, Object obj, Type type) throws JsonException {
        throw new RuntimeException();
    }
}
