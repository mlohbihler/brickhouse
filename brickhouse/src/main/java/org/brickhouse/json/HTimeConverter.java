package org.brickhouse.json;

import org.brickhouse.datatype.HTime;
import org.brickhouse.datatype.HValue;

public class HTimeConverter extends HValueConverter {
    public static final char CODE = 'h';

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
        return new HTime(value);
    }
}
