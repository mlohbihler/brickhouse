/* 
 * Copyright (c) 2015, Matthew Lohbihler
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.brickhouse.zinc;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DecimalFormat;
import java.util.Map;

import org.brickhouse.datatype.HBinary;
import org.brickhouse.datatype.HBoolean;
import org.brickhouse.datatype.HCoordinates;
import org.brickhouse.datatype.HDate;
import org.brickhouse.datatype.HDateTime;
import org.brickhouse.datatype.HGrid;
import org.brickhouse.datatype.HList;
import org.brickhouse.datatype.HMap;
import org.brickhouse.datatype.HMarker;
import org.brickhouse.datatype.HNA;
import org.brickhouse.datatype.HNumber;
import org.brickhouse.datatype.HReference;
import org.brickhouse.datatype.HRemove;
import org.brickhouse.datatype.HString;
import org.brickhouse.datatype.HTime;
import org.brickhouse.datatype.HUri;
import org.brickhouse.datatype.HValue;
import org.brickhouse.io.HGridWriter;

import com.serotonin.util.CollectionUtils;

/**
 * HZincWriter is used to write grids in the Zinc format
 *
 * @see <a href='http://project-haystack.org/doc/Zinc'>Project Haystack</a>
 */
public class ZincWriter implements HGridWriter {
    private PrintWriter out;

    //////////////////////////////////////////////////////////////////////////
    // Construction
    //////////////////////////////////////////////////////////////////////////

    public ZincWriter(Writer out) {
        if (out instanceof PrintWriter)
            this.out = (PrintWriter) out;
        else
            this.out = new PrintWriter(out);
    }

    /** Write a grid to an in-memory string */
    public static String gridToString(HGrid grid) {
        ZincGrid zg = new ZincGrid(grid);
        StringWriter out = new StringWriter(zg.getColumns().size() * zg.getRows().size() * 16);
        new ZincWriter(out).writeGrid(zg);
        return out.toString();
    }

    private ZincWriter(StringWriter out) {
        this.out = new PrintWriter(out);
    }

    //////////////////////////////////////////////////////////////////////////
    // Grid Writer
    //////////////////////////////////////////////////////////////////////////

    @Override
    public void writeGrid(HGrid grid) {
        writeGrid(new ZincGrid(grid));
    }

    private void writeGrid(ZincGrid grid) {
        // meta
        out.write("ver:\"2.0\"");
        writeMeta(grid.getMeta());
        out.write('\n');

        // cols
        boolean first = true;
        for (ZincColumn column : grid.getColumns()) {
            if (first)
                first = false;
            else
                out.write(',');
            writeCol(column);
        }
        out.write('\n');

        // rows
        for (ZincRow row : grid.getRows()) {
            writeRow(grid, row);
            out.write('\n');
        }
    }

    /** Flush underlying output stream */
    @Override
    public void flush() {
        out.flush();
    }

    /** Close underlying output stream */
    @Override
    public void close() {
        out.close();
    }

    //////////////////////////////////////////////////////////////////////////
    // Implementation
    //////////////////////////////////////////////////////////////////////////

    private void writeMeta(HMap meta) {
        if (meta != null) {
            for (Map.Entry<String, HValue> e : meta.entrySet()) {
                HValue val = e.getValue();
                out.write(' ');
                out.write(e.getKey());
                if (val != HMarker.VALUE) {
                    out.write(':');
                    out.write(toZinc(val));
                }
            }
        }
    }

    private void writeCol(ZincColumn col) {
        out.write(col.getName());
        writeMeta(col.getMeta());
    }

    private void writeRow(ZincGrid grid, ZincRow row) {
        boolean first = true;
        for (ZincColumn column : grid.getColumns()) {
            HValue val = row.get(column);
            if (!first)
                out.write(',');
            if (val == null) {
                if (first)
                    out.write('N');
            }
            else
                out.write(toZinc(val));
            first = false;
        }
    }

    //
    // Data types
    //
    private String toZinc(HValue h) {
        StringBuilder sb = new StringBuilder();
        if (h instanceof HBinary) {
            sb.append("Bin(");
            sb.append(((HBinary) h).getMime());
            sb.append(')');
        }
        else if (h instanceof HBoolean)
            sb.append(((HBoolean) h).isValue() ? 'T' : 'F');
        else if (h instanceof HCoordinates)
            sb.append(h.toString());
        else if (h instanceof HDate)
            sb.append(h.toString());
        else if (h instanceof HDateTime)
            sb.append(h.toString());
        else if (h instanceof HList) {
            // Convert the list to a string. Note that this is irreversible, and is not actually supported by Zinc.
            // It is only done here to avoid the exception below.
            // TODO commas in the entries should at least be escaped.
            String cdl = CollectionUtils.implode(((HList) h).getList(), ",");
            toZinc(sb, cdl);
        }
        else if (h instanceof HMap) {
            // TODO this is probably not right. The result would need to be escaped or something.
            HMap m = (HMap) h;
            boolean first = true;
            for (Map.Entry<String, HValue> e : m.entrySet()) {
                if (first)
                    first = false;
                else
                    sb.append(' ');
                sb.append(e.getKey());
                HValue val = e.getValue();
                if (val != HMarker.VALUE)
                    sb.append(':').append(toZinc(val));
            }
        }
        else if (h instanceof HMarker)
            sb.append('M');
        else if (h instanceof HNumber) {
            HNumber n = (HNumber) h;
            if (n.getValue() == Double.POSITIVE_INFINITY)
                sb.append("INF");
            else if (n.getValue() == Double.NEGATIVE_INFINITY)
                sb.append("-INF");
            else if (Double.isNaN(n.getValue()))
                sb.append("NaN");
            else {
                double abs = n.getValue();
                if (abs < 0)
                    abs = -abs;
                if (abs > 1.0)
                    sb.append(new DecimalFormat("#0.####").format(n.getValue()));
                else
                    sb.append(n.getValue());

                if (n.getUnit() != null)
                    sb.append(n.getUnit());
            }
        }
        else if (h instanceof HNA)
            sb.append("NA");
        else if (h instanceof HReference) {
            HReference r = (HReference) h;
            sb.append('@');
            sb.append(r.getId());
            if (r.getDis() != null) {
                sb.append(' ');
                toZinc(sb, r.getDis());
            }
        }
        else if (h instanceof HMarker)
            sb.append('M');
        else if (h instanceof HRemove)
            sb.append('R');
        else if (h instanceof HString)
            toZinc(sb, ((HString) h).getValue());
        else if (h instanceof HTime)
            sb.append(h.toString());
        else if (h instanceof HUri) {
            sb.append('`');
            sb.append(((HUri) h).getValue());
            sb.append('`');
        }
        else
            throw new RuntimeException("toZinc doesn't handle type " + h.getClass());

        return sb.toString();
    }

    private void toZinc(StringBuilder sb, String s) {
        sb.append('"');
        for (int i = 0; i < s.length(); ++i) {
            int c = s.charAt(i);
            if (c < ' ' || c == '"' || c == '\\') {
                sb.append('\\');
                switch (c) {
                case '\n':
                    sb.append('n');
                    break;
                case '\r':
                    sb.append('r');
                    break;
                case '\t':
                    sb.append('t');
                    break;
                case '"':
                    sb.append('"');
                    break;
                case '\\':
                    sb.append('\\');
                    break;
                default:
                    sb.append('u').append('0').append('0');
                    if (c <= 0xf)
                        sb.append('0');
                    sb.append(Integer.toHexString(c));
                }
            }
            else {
                sb.append((char) c);
            }
        }
        sb.append('"');
    }
}