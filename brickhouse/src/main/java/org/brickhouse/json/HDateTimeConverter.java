package org.brickhouse.json;

import org.brickhouse.datatype.HDateTime;
import org.brickhouse.datatype.HValue;

public class HDateTimeConverter extends HValueConverter {
    public static final char CODE = 't';

    @Override
    protected char getTypeCode() {
        return CODE;
    }

    @Override
    protected String toString(Object value) {
        return value.toString();
    }

    @Override
    protected HValue fromString(String value) {
        return HDateTime.parse(value);
    }
}
