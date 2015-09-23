package org.brickhouse.json;

import org.brickhouse.datatype.HUri;
import org.brickhouse.datatype.HValue;

public class HUriConverter extends HValueConverter {
    public static final char CODE = 'u';

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
        return new HUri(value);
    }
}
