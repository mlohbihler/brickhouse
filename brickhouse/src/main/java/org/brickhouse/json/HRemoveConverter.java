package org.brickhouse.json;

import org.brickhouse.datatype.HRemove;
import org.brickhouse.datatype.HValue;

public class HRemoveConverter extends HValueConverter {
    public static final char CODE = 'x';

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
        return HRemove.VALUE;
    }
}
