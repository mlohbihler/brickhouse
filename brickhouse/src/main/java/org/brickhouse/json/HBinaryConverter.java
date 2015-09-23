package org.brickhouse.json;

import org.brickhouse.datatype.HBinary;
import org.brickhouse.datatype.HValue;

public class HBinaryConverter extends HValueConverter {
    public static final char CODE = 'b';

    @Override
    protected char getTypeCode() {
        return CODE;
    }

    @Override
    protected String toString(Object value) {
        return ((HBinary) value).getMime();
    }

    @Override
    protected HValue fromString(String value) {
        return new HBinary(value);
    }
}
