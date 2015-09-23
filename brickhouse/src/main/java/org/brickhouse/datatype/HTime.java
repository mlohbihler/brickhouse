package org.brickhouse.datatype;

import java.util.Calendar;

import org.apache.commons.lang3.StringUtils;

public class HTime extends HValue implements Comparable<HTime> {
    public static final HTime MIDNIGHT = new HTime(0, 0, 0, 0);

    private final int hour;
    private final int minute;
    private final int second;
    private final int ms;

    public HTime(int hour, int minute, int second, int ms) {
        if (hour < 0 || hour > 23)
            throw new IllegalArgumentException("Invalid hour");
        if (minute < 0 || minute > 59)
            throw new IllegalArgumentException("Invalid min");
        if (second < 0 || second > 59)
            throw new IllegalArgumentException("Invalid sec");
        if (ms < 0 || ms > 999)
            throw new IllegalArgumentException("Invalid ms");

        this.hour = hour;
        this.minute = minute;
        this.second = second;
        this.ms = ms;
    }

    public HTime(int hour, int min, int sec) {
        this(hour, min, sec, 0);
    }

    public HTime(int hour, int min) {
        this(hour, min, 0, 0);
    }

    public HTime(Calendar c) {
        this(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c.get(Calendar.SECOND), c.get(Calendar.MILLISECOND));
    }

    public HTime(String s) {
        String[] parts = s.split(":");

        if (parts.length != 3)
            throw new IllegalArgumentException("Invalid HTime format");

        hour = Integer.parseInt(parts[0]);
        minute = Integer.parseInt(parts[1]);

        int dot = parts[2].indexOf(".");
        if (dot == -1) {
            second = Integer.parseInt(parts[2]);
            ms = 0;
        }
        else {
            second = Integer.parseInt(parts[2].substring(0, dot));

            String millis = parts[2].substring(dot + 1);
            if (millis.length() == 1)
                ms = Integer.parseInt(millis) * 100;
            else if (millis.length() == 2)
                ms = Integer.parseInt(millis) * 10;
            else
                ms = Integer.parseInt(millis);
        }
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public int getSecond() {
        return second;
    }

    public int getMs() {
        return ms;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + hour;
        result = prime * result + minute;
        result = prime * result + ms;
        result = prime * result + second;
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
        HTime other = (HTime) obj;
        if (hour != other.hour)
            return false;
        if (minute != other.minute)
            return false;
        if (ms != other.ms)
            return false;
        if (second != other.second)
            return false;
        return true;
    }

    @Override
    public int compareTo(HTime that) {
        HTime x = that;
        if (hour < x.hour)
            return -1;
        if (hour > x.hour)
            return 1;
        if (minute < x.minute)
            return -1;
        if (minute > x.minute)
            return 1;
        if (second < x.second)
            return -1;
        if (second > x.second)
            return 1;
        if (ms < x.ms)
            return -1;
        return ms <= x.ms ? 0 : 1;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(StringUtils.leftPad(Integer.toString(hour), 2, '0'));
        sb.append(':');
        sb.append(StringUtils.leftPad(Integer.toString(minute), 2, '0'));
        sb.append(':');
        sb.append(StringUtils.leftPad(Integer.toString(second), 2, '0'));
        if (ms != 0) {
            sb.append('.');
            String millis = StringUtils.leftPad(Integer.toString(ms), 3, '0');
            int len = 3;
            while (millis.charAt(len - 1) == '0')
                len--;
            sb.append(millis.substring(0, len));
        }
        return sb.toString();
    }

    //
    //
    //    public String toZinc()
    //    {
    ///* 112*/        StringBuffer s = new StringBuffer();
    ///* 113*/        toZinc(s);
    ///* 114*/        return s.toString();
    //    }
    //
    //
    //    void toZinc(StringBuffer s)
    //    {
    ///* 120*/        if(hour < 10)/* 120*/            s.append('0');/* 120*/        s.append(hour).append(':');
    ///* 121*/        if(min < 10)/* 121*/            s.append('0');/* 121*/        s.append(min).append(':');
    ///* 122*/        if(sec < 10)/* 122*/            s.append('0');/* 122*/        s.append(sec);
    ///* 123*/        if(ms != 0)
    //        {
    ///* 125*/            s.append('.');
    ///* 126*/            if(ms < 10)/* 126*/                s.append('0');
    ///* 127*/            if(ms < 100)/* 127*/                s.append('0');
    ///* 128*/            s.append(ms);
    //        }
    //    }
}
