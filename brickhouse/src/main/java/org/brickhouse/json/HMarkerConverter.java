package org.brickhouse.json;

import org.brickhouse.datatype.HMarker;
import org.brickhouse.datatype.HValue;

public class HMarkerConverter extends HValueConverter {
    public static final char CODE = 'm';

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
        return HMarker.VALUE;
    }
}
