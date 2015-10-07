/* 
 * Copyright (c) 2015, Matthew Lohbihler
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.brickhouse.datatype;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class HMap extends HValue {
    public static final HMap EMPTY = new HMap() {
        @Override
        public HMap merge(HMap diff) {
            throw new RuntimeException("Empty HMap is immutable");
        }

        @Override
        public HMap put(String key, String value) {
            throw new RuntimeException("Empty HMap is immutable");
        }

        @Override
        public HMap put(String key, boolean b) {
            throw new RuntimeException("Empty HMap is immutable");
        }

        @Override
        public HMap put(String key, HValue value) {
            throw new RuntimeException("Empty HMap is immutable");
        }

        @Override
        public HMap put(String key, int value) {
            throw new RuntimeException("Empty HMap is immutable");
        }

        @Override
        public HMap put(String marker) {
            throw new RuntimeException("Empty HMap is immutable");
        }

        @Override
        public HMap remove(String key) {
            throw new RuntimeException("Empty HMap is immutable");
        }

        @Override
        public HMap delete(String key) {
            throw new RuntimeException("Empty HMap is immutable");
        }
    };

    private final Map<String, HValue> map = new LinkedHashMap<>();

    public HMap() {
        // no op
    }

    public HMap(Map<String, Object> map) {
        map.putAll(map);
    }

    public HMap(HMap map) {
        this.map.putAll(map.map);
    }

    public HMap merge(HMap diff) {
        for (Entry<String, HValue> e : diff.entrySet()) {
            if (e.getValue() == HRemove.VALUE)
                map.remove(e.getKey());
            else
                map.put(e.getKey(), e.getValue());
        }
        return this;
    }

    public HMap put(String key, String value) {
        map.put(key, new HString(value));
        return this;
    }

    public HMap put(String key, boolean b) {
        map.put(key, b ? HBoolean.TRUE : HBoolean.FALSE);
        return this;
    }

    public HMap put(String key, HValue value) {
        map.put(key, value);
        return this;
    }

    public HMap put(String key, int value) {
        map.put(key, new HNumber(value));
        return this;
    }

    public HMap put(String marker) {
        map.put(marker, HMarker.VALUE);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T extends HValue> T get(String key) {
        return (T) map.get(key);
    }

    //        public int getInt(String key) {
    //            BigDecimal num = get(key);
    //            return num.intValue();
    //        }

    public HValue remove(String key) {
        return map.remove(key);
    }

    public HMap delete(String key) {
        map.remove(key);
        return this;
    }

    public Map<String, HValue> getMap() {
        return map;
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public boolean containsKey(String name) {
        return map.containsKey(name);
    }

    public boolean has(String name) {
        return map.containsKey(name);
    }

    public HReference id() {
        return get("id");
    }

    public String disOrNull() {
        HValue v = get("dis");
        if (v instanceof HString)
            return ((HString) v).getValue();
        v = get("name");
        if (v instanceof HString)
            return ((HString) v).getValue();
        return null;
    }

    public String dis() {
        String s = disOrNull();
        if (s != null)
            return s;
        HValue v = get("id");
        if (v instanceof HReference)
            return ((HReference) v).getDis();
        return "????";
    }

    public Set<String> keySet() {
        return map.keySet();
    }

    public HBinary getBinary(String name) {
        return get(name);
    }

    public boolean getBoolean(String name) {
        return getBoolean(name, false);
    }

    public boolean getBoolean(String name, boolean defaultValue) {
        HBoolean b = get(name);
        if (b == null)
            return defaultValue;
        return b.isValue();
    }

    public HCoordinates getCoordinates(String name) {
        return get(name);
    }

    public HDate getDate(String name) {
        return get(name);
    }

    public HDateTime getDateTime(String name) {
        return get(name);
    }

    public HNumber getNumber(String name) {
        return get(name);
    }

    public HReference getReference(String name) {
        return get(name);
    }

    public String getString(String name) {
        return getString(name, null);
    }

    public String getString(String name, String defaultValue) {
        HString s = get(name);
        if (s == null)
            return defaultValue;
        return s.getValue();
    }

    public HTime getTime(String name) {
        return get(name);
    }

    public HUri getUri(String name) {
        return get(name);
    }

    public int getInt(String name) {
        return getInt(name, 0);
    }

    public int getInt(String name, int defaultValue) {
        HNumber n = get(name);
        if (n == null)
            return defaultValue;
        return n.intValue();
    }

    public final double getDouble(String name) {
        return getDouble(name, 0);
    }

    public final double getDouble(String name, double defaultValue) {
        HNumber n = get(name);
        if (n == null)
            return defaultValue;
        return n.getValue();
    }

    public Set<Entry<String, HValue>> entrySet() {
        return map.entrySet();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((map == null) ? 0 : map.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!HMap.class.isAssignableFrom(obj.getClass()))
            return false;
        HMap other = (HMap) obj;
        if (map == null) {
            if (other.map != null)
                return false;
        }
        else if (!map.equals(other.map))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return map.toString();
    }

    //    public static boolean isTagName(String n)
    //    {
    ///* 161*/        if(n.length() == 0)/* 161*/            return false;
    ///* 162*/        int first = n.charAt(0);
    ///* 163*/        if(first < 97 || first > 122)/* 163*/            return false;
    ///* 164*/        for(int i = 0; i < n.length(); i++)
    //        {
    ///* 166*/            int c = n.charAt(i);
    ///* 167*/            if(c >= 128 || !tagChars[c])/* 167*/                return false;
    //        }
    ///* 169*/        return true;
    //    }
    //    static             {
    ///* 172*/        tagChars = new boolean[128];
    //
    //
    ///* 175*/        for(int i = 97; i <= 122; i++)/* 175*/            tagChars[i] = true;
    ///* 176*/        for(int i = 65; i <= 90; i++)/* 176*/            tagChars[i] = true;
    ///* 177*/        for(int i = 48; i <= 57; i++)/* 177*/            tagChars[i] = true;
    ///* 178*/        tagChars[95] = true;
    //    }
    //
}
