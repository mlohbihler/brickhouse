/* 
 * Copyright (c) 2015, Matthew Lohbihler
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.brickhouse.datatype;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.List;

public class HGrid {
    private final HMap meta;
    private final List<HMap> rows;

    public HGrid() {
        this(emptyList());
    }

    public HGrid(HMap row) {
        this(Collections.singletonList(row));
    }

    public HGrid(List<HMap> rows) {
        this(null, rows);
    }

    public HGrid(HMap meta, HMap row) {
        this(meta, Collections.singletonList(row));
    }

    public HGrid(HMap meta, List<HMap> rows) {
        this.meta = meta == null ? HMap.EMPTY : meta;
        this.rows = rows;
    }

    public HGrid(Exception e) {
        StringWriter out = new StringWriter();
        e.printStackTrace(new PrintWriter(out, true));
        String trace = out.toString();

        //        StringBuilder temp = new StringBuilder(trace.length());
        //        for (int i = 0; i < trace.length(); ++i) {
        //            int ch = trace.charAt(i);
        //            if (ch == '\t')
        //                temp.append("  ");
        //            else if (ch != '\r')
        //                temp.append((char) ch);
        //        }
        //        trace = temp.toString();

        meta = new HMap().put("err").put("dis", e.toString()).put("errTrace", trace);
        rows = emptyList();
    }

    private static List<HMap> emptyList() {
        return Collections.emptyList();
    }

    public HMap getMeta() {
        return meta;
    }

    public boolean isErr() {
        return meta.containsKey("err");
    }

    public boolean isEmpty() {
        return rows.isEmpty();
    }

    public List<HMap> getRows() {
        return rows;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((meta == null) ? 0 : meta.hashCode());
        result = prime * result + ((rows == null) ? 0 : rows.hashCode());
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
        HGrid other = (HGrid) obj;
        if (meta == null) {
            if (other.meta != null)
                return false;
        }
        else if (!meta.equals(other.meta))
            return false;
        if (rows == null) {
            if (other.rows != null)
                return false;
        }
        else if (!rows.equals(other.rows))
            return false;
        return true;
    }
}
