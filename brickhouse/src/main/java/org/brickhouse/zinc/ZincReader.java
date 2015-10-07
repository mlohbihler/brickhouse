/* 
 * Copyright (c) 2015, Matthew Lohbihler
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.brickhouse.zinc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.brickhouse.ParseException;
import org.brickhouse.datatype.HBinary;
import org.brickhouse.datatype.HBoolean;
import org.brickhouse.datatype.HCoordinates;
import org.brickhouse.datatype.HDate;
import org.brickhouse.datatype.HDateTime;
import org.brickhouse.datatype.HGrid;
import org.brickhouse.datatype.HMap;
import org.brickhouse.datatype.HMarker;
import org.brickhouse.datatype.HNA;
import org.brickhouse.datatype.HNumber;
import org.brickhouse.datatype.HReference;
import org.brickhouse.datatype.HRemove;
import org.brickhouse.datatype.HString;
import org.brickhouse.datatype.HTime;
import org.brickhouse.datatype.HTimeZone;
import org.brickhouse.datatype.HUri;
import org.brickhouse.datatype.HValue;
import org.brickhouse.filter.Filter;
import org.brickhouse.io.HGridReader;

/**
 * HZincReader reads grids using the Zinc format.
 * 
 * @see <a href='http://project-haystack.org/doc/Zinc'>Project Haystack</a>
 */
public class ZincReader implements HGridReader {

    //////////////////////////////////////////////////////////////////////////
    // Construction
    //////////////////////////////////////////////////////////////////////////

    /** Read from UTF-8 input stream. */
    public ZincReader(InputStream in) {
        try {
            this.in = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            init();
        }
        catch (IOException e) {
            throw err(e);
        }
    }

    public ZincReader(Reader in) {
        if (in instanceof BufferedReader)
            this.in = in;
        else
            this.in = new BufferedReader(in);
        init();
    }

    /** Read from in-memory string. */
    public ZincReader(String in) {
        this(new StringReader(in));
    }

    private void init() {
        consume();
        consume();
    }

    //////////////////////////////////////////////////////////////////////////
    // HGridReader
    //////////////////////////////////////////////////////////////////////////

    /** Read grid from the stream. */
    @Override
    public HGrid readGrid() {
        ZincGrid zg = readZincGrid();

        List<HMap> maps = new ArrayList<>();
        for (ZincRow row : zg.getRows()) {
            if (row.isEmpty())
                maps.add(new HMap());
            else
                maps.add(new HMap(row));
        }

        return new HGrid(zg.getMeta(), maps);
    }

    private ZincGrid readZincGrid() {
        HMap meta = new HMap();

        // meta line
        readVer();
        readMeta(meta);
        consumeNewline();

        // read cols
        List<ZincColumn> columns = new ArrayList<>();
        int numCols = 0;
        while (true) {
            String name = readId();
            skipSpace();
            numCols++;
            HMap colMeta = new HMap();
            readMeta(colMeta);
            columns.add(new ZincColumn(numCols, name, colMeta));
            if (cur != ',')
                break;
            consume();
            skipSpace();
        }
        consumeNewline();

        ZincGrid zg = new ZincGrid(meta, columns);

        // rows
        while (cur != '\n' && cur > 0) {
            HValue[] cells = new HValue[numCols];
            for (int i = 0; i < numCols; ++i) {
                skipSpace();
                if (cur != ',' && cur != '\n')
                    cells[i] = readVal();
                skipSpace();
                if (i + 1 < numCols) {
                    if (cur != ',')
                        throw errChar("Expecting comma in row");
                    consume();
                }
            }
            consumeNewline();
            zg.getRows().add(new ZincRow(zg, cells));
        }
        if (cur == '\n')
            consumeNewline();

        return zg;
    }

    /** Read list of grids from the stream. */
    public List<HGrid> readGrids() {
        List<HGrid> list = new ArrayList<>();
        while (cur > 0)
            list.add(readGrid());
        return list;
    }

    /** Close underlying input stream */
    public void close() throws IOException {
        in.close();
    }

    /** Read set of name/value tags as dictionary */
    public HMap readMap() {
        HMap map = new HMap();
        readMeta(map);
        if (cur >= 0)
            throw errChar("Expected end of stream");
        return map;
    }

    public HMap readDiff() {
        HMap map = new HMap();
        while (isIdStart(cur)) {
            // name
            String name = readId();

            // marker or :val
            HValue val = HMarker.VALUE;
            if (cur == ':') {
                consume();
                val = readVal();
            }
            map.put(name, val);

            if (cur == ',')
                consume();
        }
        if (cur >= 0)
            throw errChar("Expected end of stream");
        return map;
    }

    //////////////////////////////////////////////////////////////////////////
    // Implementation
    //////////////////////////////////////////////////////////////////////////

    private void readVer() {
        String id = readId();
        if (!id.equals("ver"))
            throw err("Expecting zinc header 'ver:2.0', not '" + id + "'");
        if (cur != ':')
            throw err("Expecting ':' colon");
        consume();
        String ver = readStrLiteral();
        if (!ver.equals("2.0"))
            //            version = 2;
            //        else
            throw err("Unsupported zinc version: " + ver);
        skipSpace();
    }

    private void readMeta(HMap meta) {
        // parse pairs
        while (isIdStart(cur)) {
            // name
            String name = readId();

            // marker or :val
            HValue val = HMarker.VALUE;
            skipSpace();
            if (cur == ':') {
                consume();
                skipSpace();
                val = readVal();
                skipSpace();
            }
            meta.put(name, val);
            skipSpace();
        }
    }

    private String readId() {
        if (!isIdStart(cur))
            throw errChar("Invalid name start char");
        StringBuffer s = new StringBuffer();
        while (isId(cur)) {
            s.append((char) cur);
            consume();
        }
        return s.toString();
    }

    //////////////////////////////////////////////////////////////////////////
    // HVals
    //////////////////////////////////////////////////////////////////////////

    /** Read scalar value. */
    public HValue readScalar() {
        HValue val = readVal();
        if (cur >= 0)
            throw errChar("Expected end of stream");
        return val;
    }

    /** Read a single scalar value from the stream. */
    private HValue readVal() {
        if (isDigit(cur))
            return readNumVal();
        if (isAlpha(cur))
            return readWordVal();
        switch (cur) {
        case '@':
            return readRefVal();
        case '"':
            return readStrVal();
        case '`':
            return readUriVal();
        case '-':
            if (peek == 'I')
                return readWordVal();
            return readNumVal();
        default:
            throw errChar("Unexpected char for start of value");
        }
    }

    private HValue readWordVal() {
        // read into string
        StringBuffer s = new StringBuffer();
        do {
            s.append((char) cur);
            consume();
        }
        while (isAlpha(cur));
        String word = s.toString();

        // match identifier
        if (isFilter) {
            if (word.equals("true"))
                return HBoolean.TRUE;
            if (word.equals("false"))
                return HBoolean.FALSE;
        }
        else {
            if (word.equals("N"))
                return null;
            if (word.equals("M"))
                return HMarker.VALUE;
            if (word.equals("R"))
                return HRemove.VALUE;
            if (word.equals("T"))
                return HBoolean.TRUE;
            if (word.equals("F"))
                return HBoolean.FALSE;
            if (word.equals("NA"))
                return HNA.VALUE;
            if (word.equals("Bin"))
                return readBinVal();
            if (word.equals("C"))
                return readCoordVal();
        }
        if (word.equals("NaN"))
            return HNumber.NaN;
        if (word.equals("INF"))
            return HNumber.POS_INF;
        if (word.equals("-INF"))
            return HNumber.NEG_INF;
        throw err("Unknown value identifier: " + word);
    }

    private HValue readBinVal() {
        if (cur < 0)
            throw err("Expected '(' after Bin");
        consume();
        StringBuffer s = new StringBuffer();
        while (cur != ')') {
            if (cur < 0)
                throw err("Unexpected end of bin literal");
            if (cur == '\n' || cur == '\r')
                throw err("Unexpected newline in bin literal");
            s.append((char) cur);
            consume();
        }
        consume();
        return new HBinary(s.toString());
    }

    private HValue readCoordVal() {
        if (cur < 0)
            throw err("Expected '(' after Coord");
        consume();
        StringBuffer s = new StringBuffer("C(");
        while (cur != ')') {
            if (cur < 0)
                throw err("Unexpected end of coord literal");
            if (cur == '\n' || cur == '\r')
                throw err("Unexpected newline in coord literal");
            s.append((char) cur);
            consume();
        }
        consume();
        s.append(")");
        return HCoordinates.parse(s.toString());
    }

    private HValue readNumVal() {
        // parse numeric part
        StringBuffer s = new StringBuffer();
        s.append((char) cur);
        consume();
        while (isDigit(cur) || cur == '.' || cur == '_') {
            if (cur != '_')
                s.append((char) cur);
            consume();
            if (cur == 'e' || cur == 'E') {
                if (peek == '-' || peek == '+' || isDigit(peek)) {
                    s.append((char) cur);
                    consume();
                    s.append((char) cur);
                    consume();
                }
            }
        }
        double val = Double.parseDouble(s.toString());

        // HDate - check for dash
        HDate date = null;
        HTime time = null;
        int hour = -1;
        if (cur == '-') {
            int year;
            try {
                year = Integer.parseInt(s.toString());
            }
            catch (Exception e) {
                throw err("Invalid year for date value: " + s);
            }
            consume(); // dash
            int month = readTwoDigits("Invalid digit for month in date value");
            if (cur != '-')
                throw errChar("Expected '-' for date value");
            consume();
            int day = readTwoDigits("Invalid digit for day in date value");
            date = new HDate(year, month, day);

            // check for 'T' date time
            if (cur != 'T')
                return date;

            // parse next two digits and drop down to HTime parsing
            consume();
            hour = readTwoDigits("Invalid digit for hour in date time value");
        }

        // HTime - check for colon
        if (cur == ':') {
            // hour (may have been parsed already in date time)
            if (hour < 0) {
                if (s.length() != 2) {
                    throw err("Hour must be two digits for time value: " + s);
                }
                try {
                    hour = Integer.parseInt(s.toString());
                }
                catch (Exception e) {
                    throw err("Invalid hour for time value: " + s);
                }
            }
            consume(); // colon
            int min = readTwoDigits("Invalid digit for minute in time value");
            if (cur != ':')
                throw errChar("Expected ':' for time value");
            consume();
            int sec = readTwoDigits("Invalid digit for seconds in time value");
            int ms = 0;
            if (cur == '.') {
                consume();
                int places = 0;
                while (isDigit(cur)) {
                    ms = (ms * 10) + (cur - '0');
                    consume();
                    places++;
                }
                switch (places) {
                case 1:
                    ms *= 100;
                    break;
                case 2:
                    ms *= 10;
                    break;
                case 3:
                    break;
                default:
                    throw err("Too many digits for milliseconds in time value");
                }
            }
            time = new HTime(hour, min, sec, ms);
            if (date == null)
                return time;
        }

        // HDateTime (if we have date and time)
        if (date != null) {
            // timezone offset "Z" or "-/+hh:mm"
            int tzOffset = 0;
            if (cur == 'Z')
                consume();
            else {
                boolean neg = cur == '-';
                if (cur != '-' && cur != '+')
                    throw errChar("Expected -/+ for timezone offset");
                consume();
                int tzHours = readTwoDigits("Invalid digit for timezone offset");
                if (cur != ':')
                    throw errChar("Expected colon for timezone offset");
                consume();
                int tzMins = readTwoDigits("Invalid digit for timezone offset");
                tzOffset = (tzHours * 3600) + (tzMins * 60);
                if (neg)
                    tzOffset = -tzOffset;
            }

            // timezone name
            HTimeZone tz;
            if (cur != ' ') {
                if (tzOffset != 0)
                    throw errChar("Expected space between timezone offset and name");
                tz = HTimeZone.UTC;
            }
            else {
                consume();
                StringBuffer tzBuf = new StringBuffer();
                if (!isTz(cur))
                    throw errChar("Expected timezone name");
                while (isTz(cur)) {
                    tzBuf.append((char) cur);
                    consume();
                }
                tz = HTimeZone.forName(tzBuf.toString());
            }
            return new HDateTime(date, time, tz, tzOffset);
        }

        // if we have unit, parse that
        String unit = null;
        if (isUnit(cur)) {
            s = new StringBuffer();
            while (isUnit(cur)) {
                s.append((char) cur);
                consume();
            }
            unit = s.toString();
        }

        return new HNumber(val, unit);
    }

    private int readTwoDigits(String errMsg) {
        if (!isDigit(cur))
            throw errChar(errMsg);
        int tens = (cur - '0') * 10;
        consume();
        if (!isDigit(cur))
            throw errChar(errMsg);
        int val = tens + (cur - '0');
        consume();
        return val;
    }

    //    private int readTimeMs() {
    //        int ms = 0;
    //        return ms;
    //    }

    private HValue readRefVal() {
        consume(); // opening @
        StringBuffer s = new StringBuffer();
        while (HReference.isIdChar(cur)) {
            if (cur < 0)
                throw err("Unexpected end of ref literal");
            if (cur == '\n' || cur == '\r')
                throw err("Unexpected newline in ref literal");
            s.append((char) cur);
            consume();
        }
        skipSpace();

        String dis = null;
        if (cur == '"')
            dis = readStrLiteral();

        return new HReference(s.toString(), dis);
    }

    private HValue readStrVal() {
        return new HString(readStrLiteral());
    }

    private String readStrLiteral() {
        consume(); // opening quote
        StringBuffer s = new StringBuffer();
        while (cur != '"') {
            if (cur < 0)
                throw err("Unexpected end of str literal");
            if (cur == '\n' || cur == '\r')
                throw err("Unexpected newline in str literal");
            if (cur == '\\') {
                s.append((char) readEscChar());
            }
            else {
                s.append((char) cur);
                consume();
            }
        }
        consume(); // closing quote
        return s.toString();
    }

    private int readEscChar() {
        consume(); // back slash

        // check basics
        switch (cur) {
        case 'b':
            consume();
            return '\b';
        case 'f':
            consume();
            return '\f';
        case 'n':
            consume();
            return '\n';
        case 'r':
            consume();
            return '\r';
        case 't':
            consume();
            return '\t';
        case '"':
            consume();
            return '"';
        case '$':
            consume();
            return '$';
        case '\\':
            consume();
            return '\\';
        }

        // check for uxxxx
        if (cur == 'u') {
            consume();
            int n3 = toNibble(cur);
            consume();
            int n2 = toNibble(cur);
            consume();
            int n1 = toNibble(cur);
            consume();
            int n0 = toNibble(cur);
            consume();
            return (n3 << 12) | (n2 << 8) | (n1 << 4) | (n0);
        }

        throw err("Invalid escape sequence: \\" + (char) cur);
    }

    private int toNibble(int c) {
        if ('0' <= c && c <= '9')
            return c - '0';
        if ('a' <= c && c <= 'f')
            return c - 'a' + 10;
        if ('A' <= c && c <= 'F')
            return c - 'A' + 10;
        throw errChar("Invalid hex char");
    }

    private HValue readUriVal() {
        consume(); // opening backtick
        StringBuffer s = new StringBuffer();

        while (true) {
            if (cur < 0)
                throw err("Unexpected end of uri literal");
            if (cur == '\n' || cur == '\r')
                throw err("Unexpected newline in uri literal");
            if (cur == '`')
                break;
            if (cur == '\\') {
                switch (peek) {
                case ':':
                case '/':
                case '?':
                case '#':
                case '[':
                case ']':
                case '@':
                case '\\':
                case '&':
                case '=':
                case ';':
                    s.append((char) cur);
                    s.append((char) peek);
                    consume();
                    consume();
                    break;
                case '`':
                    s.append('`');
                    consume();
                    consume();
                    break;
                default:
                    if (peek == 'u' || peek == '\\')
                        s.append((char) readEscChar());
                    else
                        throw err("Invalid URI escape sequence \\" + (char) peek);
                    break;
                }
            }
            else {
                s.append((char) cur);
                consume();
            }
        }
        consume(); // closing backtick
        return new HUri(s.toString());
    }

    //////////////////////////////////////////////////////////////////////////
    // HFilter
    //////////////////////////////////////////////////////////////////////////

    /** Never use directly. Use "HFilter.make" */
    public Filter readFilter() {
        isFilter = true;
        skipSpace();
        Filter q = readFilterOr();
        skipSpace();
        if (cur >= 0)
            throw errChar("Expected end of stream");
        return q;
    }

    private Filter readFilterOr() {
        Filter q = readFilterAnd();
        skipSpace();
        if (cur != 'o')
            return q;
        if (!readId().equals("or"))
            throw err("Expecting 'or' keyword");
        skipSpace();
        return q.or(readFilterOr());
    }

    private Filter readFilterAnd() {
        Filter q = readFilterAtomic();
        skipSpace();
        if (cur != 'a')
            return q;
        if (!readId().equals("and"))
            throw err("Expecting 'and' keyword");
        skipSpace();
        return q.and(readFilterAnd());
    }

    private Filter readFilterAtomic() {
        skipSpace();
        if (cur == '(')
            return readFilterParens();
        if (cur == '*') {
            consume();
            return Filter.ALL;
        }
        if (cur == '!' && peek != '=') {
            consume();
            return Filter.negate(readFilterOr());
        }

        String path = readFilterPath();
        skipSpace();

        if (path.toString().equals("not"))
            return Filter.missing(readFilterPath());

        if (cur == '=' && peek == '=') {
            consumeCmp();
            return Filter.eq(path, readVal());
        }
        if (cur == '!' && peek == '=') {
            consumeCmp();
            return Filter.ne(path, readVal());
        }
        if (cur == '<' && peek == '=') {
            consumeCmp();
            return Filter.le(path, readVal());
        }
        if (cur == '>' && peek == '=') {
            consumeCmp();
            return Filter.ge(path, readVal());
        }
        if (cur == '<') {
            consumeCmp();
            return Filter.lt(path, readVal());
        }
        if (cur == '>') {
            consumeCmp();
            return Filter.gt(path, readVal());
        }
        if (cur == '~' && peek == '~') {
            consumeCmp();
            return Filter.ilike(path, readVal());
        }
        if (cur == '~') {
            consumeCmp();
            return Filter.like(path, readVal());
        }

        return Filter.has(path);
    }

    private Filter readFilterParens() {
        consume();
        skipSpace();
        Filter q = readFilterOr();
        if (cur != ')')
            throw err("Expecting ')'");
        consume();
        return q;
    }

    private void consumeCmp() {
        consume();
        if (cur == '=' || cur == '~')
            consume();
        skipSpace();
    }

    private String readFilterPath() {
        // read first tag name
        String id = readId();

        // if not pathed, optimize for common case
        if (cur != '-' || peek != '>')
            return id;

        // parse path
        StringBuffer s = new StringBuffer().append(id);
        List<String> list = new ArrayList<>();
        list.add(id);
        while (cur == '-' || peek == '>') {
            consume();
            consume();
            id = readId();
            list.add(id);
            s.append('-').append('>').append(id);
        }
        return s.toString();
    }

    //////////////////////////////////////////////////////////////////////////
    // Char Reads
    //////////////////////////////////////////////////////////////////////////

    private ParseException errChar(String msg) {
        if (cur < 0)
            msg += " (end of stream)";
        else {
            msg += " (char=0x" + Integer.toHexString(cur);
            if (cur >= ' ')
                msg += " '" + (char) cur + "'";
            msg += ")";
        }
        return err(msg, null);
    }

    private ParseException err(String msg) {
        return err(msg, null);
    }

    private ParseException err(Throwable ex) {
        return err(ex.toString(), ex);
    }

    private ParseException err(String msg, Throwable ex) {
        return new ParseException(msg + " [Line " + lineNum + "]", ex);
    }

    private void skipSpace() {
        while (cur == ' ' || cur == '\t')
            consume();
    }

    int cur() {
        return cur;
    }

    void consumeNewline() {
        if (cur != '\n')
            throw errChar("Expecting newline");
        consume();
    }

    void consume() {
        try {
            cur = peek;
            peek = in.read();
            if (cur == '\n')
                lineNum++;
        }
        catch (IOException e) {
            throw err(e);
        }
    }

    //////////////////////////////////////////////////////////////////////////
    // Char Types
    //////////////////////////////////////////////////////////////////////////

    private static boolean isDigit(int c) {
        return c > 0 && c < 128 && (charTypes[c] & DIGIT) != 0;
    }

    private static boolean isAlpha(int c) {
        return c > 0 && c < 128 && (charTypes[c] & ALPHA) != 0;
    }

    private static boolean isUnit(int c) {
        return c > 0 && (c >= 128 || (charTypes[c] & UNIT) != 0);
    }

    private static boolean isTz(int c) {
        return c > 0 && c < 128 && (charTypes[c] & TZ) != 0;
    }

    private static boolean isIdStart(int c) {
        return c > 0 && c < 128 && (charTypes[c] & ID_START) != 0;
    }

    private static boolean isId(int c) {
        return c > 0 && c < 128 && (charTypes[c] & ID) != 0;
    }

    private static final byte[] charTypes = new byte[128];
    private static final int DIGIT = 0x01;
    private static final int ALPHA_LO = 0x02;
    private static final int ALPHA_UP = 0x04;
    private static final int ALPHA = ALPHA_UP | ALPHA_LO;
    private static final int UNIT = 0x08;
    private static final int TZ = 0x10;
    private static final int ID_START = 0x20;
    private static final int ID = 0x40;

    static {
        for (int i = '0'; i <= '9'; ++i)
            charTypes[i] = (DIGIT | TZ | ID);
        for (int i = 'a'; i <= 'z'; ++i)
            charTypes[i] = (ALPHA_LO | UNIT | TZ | ID_START | ID);
        for (int i = 'A'; i <= 'Z'; ++i)
            charTypes[i] = (ALPHA_UP | UNIT | TZ | ID);
        charTypes['%'] = UNIT;
        charTypes['_'] = UNIT | TZ | ID;
        charTypes['/'] = UNIT;
        charTypes['$'] = UNIT;
        charTypes['-'] = TZ;
        charTypes['+'] = TZ;
    }

    //////////////////////////////////////////////////////////////////////////
    // Fields
    //////////////////////////////////////////////////////////////////////////

    private Reader in;
    private int cur;
    private int peek;
    private int lineNum = 1;
    //    private int version;
    private boolean isFilter;
}