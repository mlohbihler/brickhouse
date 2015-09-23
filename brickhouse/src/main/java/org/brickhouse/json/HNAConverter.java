package org.brickhouse.json;

import org.brickhouse.datatype.HNA;
import org.brickhouse.datatype.HValue;

public class HNAConverter extends HValueConverter {
    public static final char CODE = 'z';

    @Override
    protected char getTypeCode() {
        return CODE;
    }

    @Override
    protected String toString(Object value) {
        return "";
    }

    @Override
    protected HValue fromString(String value) {
        return HNA.VALUE;
    }
}
