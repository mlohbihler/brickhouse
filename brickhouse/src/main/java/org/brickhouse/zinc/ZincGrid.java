/* 
 * Copyright (c) 2015, Matthew Lohbihler
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.brickhouse.zinc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.brickhouse.datatype.HGrid;
import org.brickhouse.datatype.HMap;
import org.brickhouse.datatype.HValue;

public class ZincGrid {
    private final HMap meta;
    private final List<ZincColumn> columns;
    private final Map<String, ZincColumn> colsByName = new HashMap<>();
    private final List<ZincRow> rows;

    public ZincGrid(HGrid grid) {
        this.meta = grid.getMeta();
        this.columns = new ArrayList<>();
        this.rows = new ArrayList<>();

        if (grid.getRows().isEmpty())
            columns.add(new ZincColumn(0, "empty", null));
        else {
            Set<String> columnNameSet = new HashSet<>();

            // Collect column names
            for (HMap map : grid.getRows()) {
                if (map == null)
                    continue;

                for (String key : map.keySet())
                    columnNameSet.add(key);
            }

            // If all maps were null, handle special case by creating a dummy column
            if (columnNameSet.isEmpty())
                columnNameSet.add("empty");

            List<String> columnNames = new ArrayList<>(columnNameSet);
            //            Collections.sort(columnNames);

            // Create the column list and lookup
            for (int i = 0; i < columnNames.size(); i++) {
                ZincColumn col = new ZincColumn(i, columnNames.get(i), null);
                columns.add(col);
                colsByName.put(col.getName(), col);
            }

            // Now map the rows
            for (HMap map : grid.getRows()) {
                HValue[] cells = new HValue[columnNames.size()];
                for (int i = 0; i < columnNames.size(); i++) {
                    if (map == null)
                        cells[i] = null;
                    else
                        cells[i] = map.get(columnNames.get(i));
                }
                this.rows.add(new ZincRow(this, cells));
            }
        }
    }

    ZincGrid(HMap meta, List<ZincColumn> columns) {
        this.meta = meta == null ? HMap.EMPTY : meta;
        this.columns = columns;
        this.rows = new ArrayList<>();

        for (ZincColumn column : columns) {
            if (colsByName.get(column.getName()) != null)
                throw new IllegalStateException("Duplicate col name: " + column.getName());
            colsByName.put(column.getName(), column);
        }
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

    public List<ZincRow> getRows() {
        return rows;
    }

    public List<ZincColumn> getColumns() {
        return columns;
    }

    public ZincColumn getColumn(String name) {
        return colsByName.get(name);
    }
    //
    //    ////////////////////////////////////////////////////////////////////////////
    //    ////Debug
    //    ////////////////////////////////////////////////////////////////////////////
    //    //
    //    ///** Convenience for "dump(stdout)". */
    //    //public void dump()
    //    //{
    //    //dump(new PrintWriter(System.out));
    //    //}
    //    //
    //    ///** Debug dump - this is Zinc right now. */
    //    //public void dump(PrintWriter out)
    //    //{
    //    //out.println(HZincWriter.gridToString(this));
    //    //out.flush();
    //    //}
}
