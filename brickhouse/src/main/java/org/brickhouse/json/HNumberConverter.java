package org.brickhouse.json;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.brickhouse.datatype.HNumber;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonReader;
import com.serotonin.json.JsonWriter;
import com.serotonin.json.spi.ClassConverter;
import com.serotonin.json.type.JsonNumber;
import com.serotonin.json.type.JsonTypeWriter;
import com.serotonin.json.type.JsonValue;

public class HNumberConverter implements ClassConverter {
    public static final char CODE = 'n';

    @Override
    public JsonValue jsonWrite(JsonTypeWriter writer, Object value) throws JsonException {
        throw new RuntimeException();
    }

    @Override
    public void jsonWrite(JsonWriter writer, Object value) throws IOException, JsonException {
        HNumber h = (HNumber) value;

        // Check if we can just write a number.
        if (StringUtils.isEmpty(h.getUnit()) && h != HNumber.NaN && h != HNumber.NEG_INF && h != HNumber.POS_INF)
            writer.append((new DecimalFormat("#0.####", new DecimalFormatSymbols(Locale.ENGLISH))).format(h.getValue()));
        else {
            StringBuilder sb = new StringBuilder().append(CODE).append(HValueConverter.ESCAPE);
            if (Double.isNaN(h.getValue()))
                sb.append("NaN");
            else if (h.getValue() == Double.NEGATIVE_INFINITY)
                sb.append("-INF");
            else if (h.getValue() == Double.POSITIVE_INFINITY)
                sb.append("INF");
            else
                sb.append((new DecimalFormat("#0.####", new DecimalFormatSymbols(Locale.ENGLISH))).format(h.getValue()));

            if (!StringUtils.isEmpty(h.getUnit()))
                sb.append(h.getUnit());

            writer.quote(sb.toString());
        }
    }

    @Override
    public Object jsonRead(JsonReader reader, JsonValue jsonValue, Type type) throws JsonException {
        if (jsonValue instanceof JsonNumber)
            return new HNumber(((JsonNumber) jsonValue).doubleValue());

        // Convert from string representation
        String s = jsonValue.toString().substring(2);

        double d;
        if (s.startsWith("NaN")) {
            d = Double.NaN;
            s = s.substring(3);
        }
        else if (s.startsWith("-INF")) {
            d = Double.NEGATIVE_INFINITY;
            s = s.substring(4);
        }
        else if (s.startsWith("INF")) {
            d = Double.POSITIVE_INFINITY;
            s = s.substring(3);
        }
        else {
            // Find the first non-numeric-like character.
            int pos = 0;
            boolean found = false;
            for (; pos < s.length(); pos++) {
                char c = s.charAt(pos);
                switch (c) {
                case '-':
                case '.':
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    break;
                default:
                    found = true;
                }
                if (found)
                    break;
            }

            d = Double.parseDouble(s.substring(0, pos));
            s = s.substring(pos);
        }

        if (StringUtils.isEmpty(s))
            return new HNumber(d);
        return new HNumber(d, s);
    }

    @Override
    public void jsonRead(JsonReader reader, JsonValue jsonValue, Object obj, Type type) throws JsonException {
        throw new RuntimeException();
    }
}
