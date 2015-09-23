package org.brickhouse.json;

import org.brickhouse.datatype.HReference;
import org.brickhouse.datatype.HValue;

import com.serotonin.json.JsonException;

public class HReferenceDBConverter extends HValueConverter {
    public static final char CODE = 'r';

    @Override
    protected char getTypeCode() {
        return CODE;
    }

    @Override
    protected String toString(Object value) {
        return ((HReference) value).getId();
    }

    @Override
    protected HValue fromString(String value) throws JsonException {
        return new HReference(value);
    }
}
