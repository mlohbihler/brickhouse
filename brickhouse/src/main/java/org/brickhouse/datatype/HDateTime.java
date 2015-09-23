package org.brickhouse.datatype;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.brickhouse.ParseException;
import org.brickhouse.zinc.ZincReader;

public class HDateTime extends HValue implements Comparable<HDateTime> {
    public static HDateTime parse(String s) {
        HValue val = (new ZincReader(s)).readScalar();
        if (val instanceof HDateTime)
            return (HDateTime) val;
        throw new ParseException(s);
    }

    private final HDate date;
    private final HTime time;
    private final int tzOffset;
    private final HTimeZone tz;
    // This represents the millis since epoch, not the ms as in the time field.
    private volatile long millis;

    private static final TimeZone utc = TimeZone.getTimeZone("Etc/UTC");

    public HDateTime(HDate date, HTime time, HTimeZone tz, int tzOffset) {
        if (date == null || time == null || tz == null)
            throw new IllegalArgumentException("null args");

        this.date = date;
        this.time = time;
        this.tz = tz;
        this.tzOffset = tzOffset;
    }

    public HDateTime(HDate date, HTime time, HTimeZone tz) {
        GregorianCalendar c = new GregorianCalendar(date.getYear(), date.getMonth() - 1, date.getDay(), time.getHour(),
                time.getMinute(), time.getSecond());
        if (time.getMs() != 0)
            c.set(Calendar.MILLISECOND, time.getMs());
        c.setTimeZone(tz.java);

        int tzOffset = c.get(Calendar.ZONE_OFFSET) / 1000 + c.get(Calendar.DST_OFFSET) / 1000;

        long millis = c.getTimeInMillis();

        this.date = date;
        this.time = time;
        this.tz = tz;
        this.tzOffset = tzOffset;
        this.millis = millis;
    }

    public HDateTime(int year, int month, int day, int hour, int min, int sec, HTimeZone tz, int tzOffset) {
        this(new HDate(year, month, day), new HTime(hour, min, sec), tz, tzOffset);
    }

    public HDateTime(int year, int month, int day, int hour, int min, HTimeZone tz, int tzOffset) {
        this(new HDate(year, month, day), new HTime(hour, min), tz, tzOffset);
    }

    public HDateTime(long millis) {
        this(millis, HTimeZone.DEFAULT);
    }

    public HDateTime(long millis, HTimeZone tz) {
        Calendar c = new GregorianCalendar(tz.java);
        c.setTimeInMillis(millis);

        int tzOffset = c.get(15) / 1000 + c.get(16) / 1000;

        this.date = new HDate(c);
        this.time = new HTime(c);
        this.tz = tz;
        this.tzOffset = tzOffset;
        this.millis = millis;
    }

    public static HDateTime now() {
        return new HDateTime(System.currentTimeMillis());
    }

    public static HDateTime now(HTimeZone tz) {
        return new HDateTime(System.currentTimeMillis(), tz);
    }

    public HDate getDate() {
        return date;
    }

    public HTime getTime() {
        return time;
    }

    public int getTzOffset() {
        return tzOffset;
    }

    public HTimeZone getTz() {
        return tz;
    }

    public long getMillis() {
        if (millis <= 0L) {
            GregorianCalendar c = new GregorianCalendar(date.getYear(), date.getMonth() - 1, date.getDay(),
                    time.getHour(), time.getMinute(), time.getSecond());
            c.setTimeZone(utc);
            c.set(Calendar.MILLISECOND, time.getMs());
            c.set(Calendar.ZONE_OFFSET, tzOffset * 1000);
            millis = c.getTimeInMillis();
        }
        return millis;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((date == null) ? 0 : date.hashCode());
        result = prime * result + ((time == null) ? 0 : time.hashCode());
        result = prime * result + ((tz == null) ? 0 : tz.hashCode());
        result = prime * result + tzOffset;
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
        HDateTime other = (HDateTime) obj;
        if (date == null) {
            if (other.date != null)
                return false;
        }
        else if (!date.equals(other.date))
            return false;
        if (time == null) {
            if (other.time != null)
                return false;
        }
        else if (!time.equals(other.time))
            return false;
        if (tz == null) {
            if (other.tz != null)
                return false;
        }
        else if (!tz.equals(other.tz))
            return false;
        if (tzOffset != other.tzOffset)
            return false;
        return true;
    }

    @Override
    public int compareTo(HDateTime that) {
        long thisMillis = getMillis();
        long thatMillis = that.getMillis();
        if (thisMillis < thatMillis)
            return -1;
        if (thisMillis > thatMillis)
            return 1;
        return 0;
    }

    @Override
    public String toString() {
        StringBuffer s = new StringBuffer();
        s.append(date);
        s.append('T');
        s.append(time);
        if (tzOffset == 0)
            s.append('Z');
        else {
            int offset = tzOffset;
            if (offset < 0) {
                s.append('-');
                offset = -offset;
            }
            else
                s.append('+');

            int zh = offset / 3600;
            int zm = (offset % 3600) / 60;
            if (zh < 10)
                s.append('0');
            s.append(zh).append(':');
            if (zm < 10)
                s.append('0');
            s.append(zm);
        }
        s.append(' ').append(tz);

        return s.toString();
    }
}
