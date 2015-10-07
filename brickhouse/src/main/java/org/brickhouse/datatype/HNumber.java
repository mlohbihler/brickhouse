/* 
 * Copyright (c) 2015, Matthew Lohbihler
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.brickhouse.datatype;

public class HNumber extends HValue implements Comparable<HNumber> {
    public static final HNumber ZERO = new HNumber(0.0D, null);
    public static final HNumber POS_INF = new HNumber((1.0D / 0.0D), null);
    public static final HNumber NEG_INF = new HNumber((-1.0D / 0.0D), null);
    public static final HNumber NaN = new HNumber((0.0D / 0.0D), null);

    private final double value;
    private final String unit;

    public HNumber(int value) {
        this(value, null);
    }

    public HNumber(int value, String unit) {
        this.value = value;
        this.unit = unit;
    }

    public HNumber(long value) {
        this(value, null);
    }

    public HNumber(long value, String unit) {
        this.value = value;
        this.unit = unit;
    }

    public HNumber(double val) {
        this(val, null);
    }

    public HNumber(double value, String unit) {
        if (!isUnitName(unit))
            throw new IllegalArgumentException("Invalid unit name: " + unit);

        this.value = value;
        this.unit = unit;
    }

    public double getValue() {
        return value;
    }

    public int intValue() {
        return (int) value;
    }

    public long longValue() {
        return (long) value;
    }

    public String getUnit() {
        return unit;
    }

    @Override
    public String toString() {
        if (unit == null)
            return Double.toString(value);
        return "" + value + unit;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((unit == null) ? 0 : unit.hashCode());
        long temp;
        temp = Double.doubleToLongBits(value);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        HNumber other = (HNumber) obj;
        if (unit == null) {
            if (other.unit != null)
                return false;
        }
        else if (!unit.equals(other.unit))
            return false;
        if (Double.doubleToLongBits(value) != Double.doubleToLongBits(other.value))
            return false;
        return true;
    }

    @Override
    public int compareTo(HNumber that) {
        if (value < that.value)
            return -1;
        if (value > that.value)
            return 1;
        return 0;
    }

    //    public long millis()
    //    {
    ///* 146*/        String u = unit;
    ///* 147*/        if(u == null)/* 147*/            u = "null";
    ///* 148*/        if(u.equals("ms") || u.equals("millisecond"))/* 148*/            return (long)val;
    ///* 149*/        if(u.equals("s") || u.equals("sec") || u.equals("millisecond"))/* 149*/            return (long)(val * 1000D);
    ///* 150*/        if(u.equals("min") || u.equals("minute"))/* 150*/            return (long)(val * 1000D * 60D);
    ///* 151*/        if(u.equals("h") || u.equals("hr") || u.equals("minute"))/* 151*/            return (long)(val * 1000D * 60D * 60D);
    ///* 152*/        else/* 152*/            throw new IllegalStateException("Invalid duration unit: " + u);
    //    }

    private static boolean unitChars[];

    public static boolean isUnitName(String unit) {
        if (unit == null)
            return true;
        if (unit.length() == 0)
            return false;
        for (int i = 0; i < unit.length(); i++) {
            int c = unit.charAt(i);
            if (c < 128 && !unitChars[c])
                return false;
        }
        return true;
    }

    static {
        unitChars = new boolean[128];

        for (int i = 97; i <= 122; i++)
            unitChars[i] = true;
        for (int i = 65; i <= 90; i++)
            unitChars[i] = true;
        unitChars[95] = true;
        unitChars[36] = true;
        unitChars[37] = true;
        unitChars[47] = true;
    }
}
