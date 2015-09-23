package org.brickhouse.json;

import org.brickhouse.datatype.HDate;
import org.brickhouse.datatype.HValue;

public class HDateConverter extends HValueConverter {
    public static final char CODE = 'd';

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
        return new HDate(value);
    }
}
