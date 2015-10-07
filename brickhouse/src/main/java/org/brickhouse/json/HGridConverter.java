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
import java.util.Collections;
import java.util.List;

import org.brickhouse.datatype.HGrid;
import org.brickhouse.datatype.HMap;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonReader;
import com.serotonin.json.JsonWriter;
import com.serotonin.json.ObjectWriter;
import com.serotonin.json.convert.ObjectJsonWriter;
import com.serotonin.json.spi.ClassConverter;
import com.serotonin.json.type.JsonTypeWriter;
import com.serotonin.json.type.JsonValue;
import com.serotonin.json.util.TypeDefinition;

public class HGridConverter implements ClassConverter {
    @Override
    public JsonValue jsonWrite(JsonTypeWriter writer, Object value) throws JsonException {
        throw new RuntimeException();
    }

    @Override
    public void jsonWrite(JsonWriter writer, Object value) throws IOException, JsonException {
        HGrid grid = (HGrid) value;

        ObjectWriter objectWriter = new ObjectJsonWriter(writer);

        if (!grid.getMeta().isEmpty())
            objectWriter.writeEntry("meta", grid.getMeta());

        objectWriter.writeEntry("rows", grid.getRows());

        objectWriter.finish();
    }

    @Override
    public Object jsonRead(JsonReader reader, JsonValue jsonValue, Type type) throws JsonException {
        HMap meta = reader.read(HMap.class, jsonValue.getJsonValue("meta"));
        @SuppressWarnings("unchecked")
        List<HMap> list = (List<HMap>) reader.read(new TypeDefinition(List.class, HMap.class),
                jsonValue.getJsonValue("rows"));
        if (list == null)
            list = Collections.emptyList();

        return new HGrid(meta, list);
    }

    @Override
    public void jsonRead(JsonReader reader, JsonValue jsonValue, Object obj, Type type) throws JsonException {
        throw new RuntimeException();
    }
}