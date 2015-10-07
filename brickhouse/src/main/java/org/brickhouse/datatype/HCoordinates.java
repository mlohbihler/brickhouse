/* 
 * Copyright (c) 2015, Matthew Lohbihler
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.brickhouse.datatype;

import java.text.DecimalFormat;

import org.brickhouse.ParseException;

public class HCoordinates extends HValue {
    private final int ulat;
    private final int ulng;

    public static HCoordinates parse(String s) {
        try {
            if (!s.startsWith("C("))
                throw new Exception();
            if (!s.endsWith(")"))
                throw new Exception();
            int comma = s.indexOf(',');
            if (comma < 3)
                throw new Exception();
            String lat = s.substring(2, comma);
            String lng = s.substring(comma + 1, s.length() - 1);
            return new HCoordinates(Double.parseDouble(lat), Double.parseDouble(lng));
        }
        catch (Exception e) {
            throw new ParseException(s);
        }
    }

    public HCoordinates(double lat, double lng) {
        this.ulat = (int) (lat * 1000000D);
        this.ulng = (int) (lng * 1000000D);

        if (ulat < -90000000 || ulat > 90000000)
            throw new IllegalArgumentException("Invalid lat > +/- 90");
        if (ulng < -180000000 || ulng > 180000000)
            throw new IllegalArgumentException("Invalid lng > +/- 180");
    }

    public static boolean isLat(double lat) {
        return -90D <= lat && lat <= 90D;
    }

    public static boolean isLng(double lng) {
        return -180D <= lng && lng <= 180D;
    }

    public double getLat() {
        return ulat / 1000000D;
    }

    public double getLng() {
        return ulng / 1000000D;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ulat;
        result = prime * result + ulng;
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
        HCoordinates other = (HCoordinates) obj;
        if (ulat != other.ulat)
            return false;
        if (ulng != other.ulng)
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuffer s = new StringBuffer();
        s.append("C(");
        uToStr(s, ulat);
        s.append(',');
        uToStr(s, ulng);
        s.append(")");
        return s.toString();
    }

    private void uToStr(StringBuffer s, int ud) {
        if (ud < 0) {
            s.append('-');
            ud = -ud;
        }
        if (ud < 1000000.0) {
            s.append(new DecimalFormat("0.0#####").format(ud / 1000000.0));
            return;
        }
        String x = String.valueOf(ud);
        int dot = x.length() - 6;
        int end = x.length();
        while (end > dot + 1 && x.charAt(end - 1) == '0')
            --end;
        for (int i = 0; i < dot; ++i)
            s.append(x.charAt(i));
        s.append('.');
        for (int i = dot; i < end; ++i)
            s.append(x.charAt(i));
    }
}
