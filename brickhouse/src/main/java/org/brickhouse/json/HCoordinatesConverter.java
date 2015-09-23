package org.brickhouse.json;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.brickhouse.datatype.HCoordinates;
import org.brickhouse.datatype.HValue;

import com.serotonin.json.JsonException;

public class HCoordinatesConverter extends HValueConverter {
    public static final char CODE = 'c';

    private static final Pattern PATTERN = Pattern.compile("\\((-?[\\d\\.]+),(-?[\\d\\.]+)\\)");

    @Override
    protected char getTypeCode() {
        return CODE;
    }

    @Override
    protected String toString(Object value) {
        HCoordinates c = (HCoordinates) value;
        return "(" + c.getLat() + "," + c.getLng() + ")";
    }

    @Override
    protected HValue fromString(String value) throws JsonException {
        Matcher matcher = PATTERN.matcher(value);
        if (!matcher.find())
            throw new JsonException("Bad coord string");
        if (matcher.groupCount() != 2)
            throw new JsonException("Bad coord group count");
        return new HCoordinates(Double.parseDouble(matcher.group(1)), Double.parseDouble(matcher.group(2)));
    }
}
