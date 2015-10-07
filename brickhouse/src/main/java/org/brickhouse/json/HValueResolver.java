/* 
 * Copyright (c) 2015, Matthew Lohbihler
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.brickhouse.json;

import java.lang.reflect.Type;

import org.brickhouse.datatype.HBinary;
import org.brickhouse.datatype.HBoolean;
import org.brickhouse.datatype.HCoordinates;
import org.brickhouse.datatype.HDate;
import org.brickhouse.datatype.HDateTime;
import org.brickhouse.datatype.HList;
import org.brickhouse.datatype.HMap;
import org.brickhouse.datatype.HMarker;
import org.brickhouse.datatype.HNumber;
import org.brickhouse.datatype.HReference;
import org.brickhouse.datatype.HRemove;
import org.brickhouse.datatype.HString;
import org.brickhouse.datatype.HTime;
import org.brickhouse.datatype.HUri;

import com.serotonin.json.JsonException;
import com.serotonin.json.spi.TypeResolver;
import com.serotonin.json.type.JsonArray;
import com.serotonin.json.type.JsonBoolean;
import com.serotonin.json.type.JsonNumber;
import com.serotonin.json.type.JsonObject;
import com.serotonin.json.type.JsonString;
import com.serotonin.json.type.JsonValue;

public class HValueResolver implements TypeResolver {
    @Override
    public Type resolve(JsonValue jsonValue) throws JsonException {
        if (jsonValue instanceof JsonString) {
            String s = jsonValue.toString();
            if (s.length() < 2 || s.charAt(1) != ':')
                return HString.class;

            switch (s.charAt(0)) {
            case HBinaryConverter.CODE:
                return HBinary.class;
            case HCoordinatesConverter.CODE:
                return HCoordinates.class;
            case HDateConverter.CODE:
                return HDate.class;
            case HDateTimeConverter.CODE:
                return HDateTime.class;
            case HMarkerConverter.CODE:
                return HMarker.class;
            case HNumberConverter.CODE:
                return HNumber.class;
            case HReferenceConverter.CODE:
                return HReference.class;
            case HRemoveConverter.CODE:
                return HRemove.class;
            case HStringConverter.CODE:
                return HString.class;
            case HTimeConverter.CODE:
                return HTime.class;
            case HUriConverter.CODE:
                return HUri.class;
            }
        }

        if (jsonValue instanceof JsonObject)
            //            return HGrid.class;
            return HMap.class;

        if (jsonValue instanceof JsonArray)
            return HList.class;

        if (jsonValue instanceof JsonBoolean)
            return HBoolean.class;

        if (jsonValue instanceof JsonNumber)
            return HNumber.class;

        throw new RuntimeException("Badly formed HVal: " + jsonValue.toString());
    }
}
