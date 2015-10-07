/* 
 * Copyright (c) 2015, Matthew Lohbihler
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.brickhouse.datatype;

import java.util.Calendar;

import org.apache.commons.lang3.StringUtils;

public class HDate extends HValue implements Comparable<HDate> {
    private final int year;
    private final int month;
    private final int day;

    public HDate(int year, int month, int day) {
        if (year < 1900)
            throw new IllegalArgumentException("Invalid year");
        if (month < 1 || month > 12)
            throw new IllegalArgumentException("Invalid month");
        if (day < 1 || day > 31)
            throw new IllegalArgumentException("Invalid day");

        this.year = year;
        this.month = month;
        this.day = day;
    }

    public HDate(Calendar c) {
        this(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DATE));
    }

    public HDate(String s) {
        String[] parts = s.split("-");

        if (parts.length != 3)
            throw new IllegalArgumentException("Invalid HDate format");

        year = Integer.parseInt(parts[0]);
        month = Integer.parseInt(parts[1]);
        day = Integer.parseInt(parts[2]);
    }

    public static HDate today() {
        return HDateTime.now().getDate();
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(StringUtils.leftPad(Integer.toString(year), 4, '0'));
        sb.append('-');
        sb.append(StringUtils.leftPad(Integer.toString(month), 2, '0'));
        sb.append('-');
        sb.append(StringUtils.leftPad(Integer.toString(day), 2, '0'));
        return sb.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + day;
        result = prime * result + month;
        result = prime * result + year;
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
        HDate other = (HDate) obj;
        if (day != other.day)
            return false;
        if (month != other.month)
            return false;
        if (year != other.year)
            return false;
        return true;
    }

    @Override
    public int compareTo(HDate that) {
        HDate x = that;
        if (year < x.year)
            return -1;
        if (year > x.year)
            return 1;
        if (month < x.month)
            return -1;
        if (month > x.month)
            return 1;
        if (day < x.day)
            return -1;
        return day <= x.day ? 0 : 1;
    }

    //    public String toZinc()
    //    {
    ///*  97*/        StringBuffer s = new StringBuffer();
    ///*  98*/        toZinc(s);
    ///*  99*/        return s.toString();
    //    }
    //
    //
    //    void toZinc(StringBuffer s)
    //    {
    ///* 105*/        s.append(year).append('-');
    ///* 106*/        if(month < 10)/* 106*/            s.append('0');/* 106*/        s.append(month).append('-');
    ///* 107*/        if(day < 10)/* 107*/            s.append('0');/* 107*/        s.append(day);
    //    }
    //
    //
    //    public HDateTime midnight(HTimeZone tz)
    //    {
    ///* 113*/        return HDateTime.make(this, HTime.MIDNIGHT, tz);
    //    }
    //
    //
    //    public HDate plusDays(int numDays)
    //    {
    ///* 119*/        if(numDays == 0)/* 119*/            return this;
    ///* 120*/        if(numDays < 0)/* 120*/            return minusDays(-numDays);
    ///* 121*/        int year = this.year;
    ///* 122*/        int month = this.month;
    ///* 123*/        int day = this.day;
    ///* 124*/        for(; numDays > 0; numDays--)
    //        {
    ///* 126*/            if(++day <= daysInMonth(year, month))
    //
    //
    ///* 129*/                continue;/* 129*/            day = 1;
    ///* 130*/            if(++month > 12)
    //            {/* 131*/                month = 1;/* 131*/                year++;
    //            }                }
    //
    ///* 134*/        return make(year, month, day);
    //    }
    //
    //
    //    public HDate minusDays(int numDays)
    //    {
    ///* 140*/        if(numDays == 0)/* 140*/            return this;
    ///* 141*/        if(numDays < 0)/* 141*/            return plusDays(-numDays);
    ///* 142*/        int year = this.year;
    ///* 143*/        int month = this.month;
    ///* 144*/        int day = this.day;
    ///* 145*/        for(; numDays > 0; numDays--)
    //        {
    ///* 147*/            if(--day > 0)
    //
    //
    ///* 150*/                continue;/* 150*/            if(--month < 1)
    //            {/* 151*/                month = 12;/* 151*/                year--;
    //            }/* 152*/            day = daysInMonth(year, month);
    //        }
    //
    ///* 155*/        return make(year, month, day);
    //    }
    //
    //
    //    public static boolean isLeapYear(int year)
    //    {
    ///* 161*/        if((year & 3) != 0)/* 161*/            return false;
    ///* 162*/        else/* 162*/            return year % 100 != 0 || year % 400 == 0;
    //    }
    //
    //
    //    public static int daysInMonth(int year, int mon)
    //    {
    ///* 168*/        return isLeapYear(year) ? daysInMonLeap[mon] : daysInMon[mon];
    //    }
    //
    //
    //    public int weekday()
    //    {
    ///* 174*/        GregorianCalendar c = new GregorianCalendar(year, month - 1, day);
    ///* 175*/        return c.get(7);
    //    }
    //    private static final int daysInMon[] = {
    ///* 178*/        -1, 31, 28, 31, 30, 31, 30, 31, 31, 30, /* 178*/        31, 30, 31
    //    private static final int daysInMonLeap[] = {            };/* 179*/        -1, 31, 29, 31, 30, 31, 30, 31, 31, 30, /* 179*/        31, 30, 31
    //    };
    //

}
