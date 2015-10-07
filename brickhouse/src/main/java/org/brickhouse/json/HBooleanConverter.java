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

import org.brickhouse.datatype.HBoolean;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonReader;
import com.serotonin.json.JsonWriter;
import com.serotonin.json.spi.ClassConverter;
import com.serotonin.json.type.JsonTypeWriter;
import com.serotonin.json.type.JsonValue;

public class HBooleanConverter implements ClassConverter {
    @Override
    public JsonValue jsonWrite(JsonTypeWriter writer, Object value) throws JsonException {
        throw new RuntimeException();
    }

    @Override
    public void jsonWrite(JsonWriter writer, Object value) throws IOException, JsonException {
        writer.append(value.toString());
    }

    @Override
    public Object jsonRead(JsonReader reader, JsonValue jsonValue, Type type) throws JsonException {
        if (jsonValue.toBoolean())
            return HBoolean.TRUE;
        return HBoolean.FALSE;
    }

    @Override
    public void jsonRead(JsonReader reader, JsonValue jsonValue, Object obj, Type type) throws JsonException {
        throw new RuntimeException();
    }
}
