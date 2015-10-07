/* 
 * Copyright (c) 2015, Matthew Lohbihler
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.brickhouse.datatype;

import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * HTimeZone handles the mapping between Haystack timezone
 * names and Java timezones.
 *
 * @see <a href='http://project-haystack.org/doc/TimeZones'>Project Haystack</a>
 */
public final class HTimeZone {
    public static HTimeZone getDefault() {
        return DEFAULT;
    }

    public static void setDefault(HTimeZone tz) {
        if (tz == null)
            throw new NullPointerException();
        DEFAULT = tz;
    }

    /** Convenience for fromName(name, true) */
    public static HTimeZone forName(String name) {
        return forName(name, true);
    }

    /**
     * Construct with Haystack timezone name, raise exception or
     * return null on error based on check flag.
     */
    public static HTimeZone forName(String name, boolean checked) {
        synchronized (cache) {
            // lookup in cache
            HTimeZone tz = cache.get(name);
            if (tz != null)
                return tz;

            // map haystack id to Java full id
            String javaId = toJava.get(name);
            if (javaId == null) {
                if (checked)
                    throw new RuntimeException("Unknown tz: " + name);
                return null;
            }

            // resolve full id to HTimeZone and cache
            TimeZone java = TimeZone.getTimeZone(javaId);
            tz = new HTimeZone(name, java);
            cache.put(name, tz);
            return tz;
        }
    }

    public static HTimeZone fromJava(TimeZone java) {
        return fromJava(java, true);
    }

    /**
     * Construct from Java timezone. Throw exception or return
     * null based on checked flag.
     */
    public static HTimeZone fromJava(TimeZone java, boolean checked) {
        String name = fromJava.get(java.getID());
        if (name != null)
            return forName(name);
        if (checked)
            throw new RuntimeException("Invalid Java timezone: " + java.getID());
        return null;
    }

    /** Private constructor */
    private HTimeZone(String name, TimeZone java) {
        this.name = name;
        this.java = java;
    }

    /** Haystack timezone name */
    public final String name;

    /** Java representation of this timezone. */
    public final TimeZone java;

    /** Return Haystack timezone name */
    @Override
    public String toString() {
        return name;
    }

    // haystack name -> HTimeZone
    private static Map<String, HTimeZone> cache = new HashMap<>();

    // haystack name <-> java name mapping
    private static Map<String, String> toJava;
    private static Map<String, String> fromJava;

    static {
        Map<String, String> toJava = new HashMap<>();
        Map<String, String> fromJava = new HashMap<>();
        try {
            // only time zones which start with these
            // regions are considered valid timezones
            Map<String, String> regions = new HashMap<>();
            regions.put("Africa", "ok");
            regions.put("America", "ok");
            regions.put("Antarctica", "ok");
            regions.put("Asia", "ok");
            regions.put("Atlantic", "ok");
            regions.put("Australia", "ok");
            regions.put("Etc", "ok");
            regions.put("Europe", "ok");
            regions.put("Indian", "ok");
            regions.put("Pacific", "ok");

            // iterate Java timezone IDs available
            String[] ids = TimeZone.getAvailableIDs();
            for (int i = 0; i < ids.length; ++i) {
                String java = ids[i];

                // skip ids not formatted as Region/City
                int slash = java.indexOf('/');
                if (slash < 0)
                    continue;
                String region = java.substring(0, slash);
                if (regions.get(region) == null)
                    continue;

                // get city name as haystack id
                slash = java.lastIndexOf('/');
                String haystack = java.substring(slash + 1);

                // store mapping b/w Java <-> Haystack
                toJava.put(haystack, java);
                fromJava.put(java, haystack);
            }
        }
        catch (Throwable e) {
            e.printStackTrace();
        }
        HTimeZone.toJava = toJava;
        HTimeZone.fromJava = fromJava;
    }

    /** UTC timezone */
    public static final HTimeZone UTC;

    /** Default timezone for VM */
    static HTimeZone DEFAULT;

    static {
        HTimeZone utc = null;
        try {
            utc = HTimeZone.fromJava(TimeZone.getTimeZone("Etc/UTC"));
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        HTimeZone def = null;
        try {
            // check if configured with system property
            String defName = System.getProperty("haystack.tz");
            if (defName != null) {
                def = HTimeZone.forName(defName, false);
                if (def == null)
                    System.out.println("WARN: invalid haystack.tz system property: " + defName);
            }

            // if we still don't have a default, try to use Java's
            if (def == null)
                def = HTimeZone.fromJava(TimeZone.getDefault());
        }
        catch (Exception e) {
            // fallback to UTC
            e.printStackTrace();
            def = utc;
        }

        DEFAULT = def;
        UTC = utc;
    }
}