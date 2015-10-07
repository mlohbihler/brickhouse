/* 
 * Copyright (c) 2015, Matthew Lohbihler
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.brickhouse.zinc;

import org.brickhouse.datatype.HMap;
import org.brickhouse.datatype.HValue;

public class ZincRow extends HMap {
    private ZincGrid grid;
    private final HValue[] cells;

    public ZincRow(ZincGrid grid, HValue[] cells) {
        this.grid = grid;
        this.cells = cells;
        for (int i = 0; i < cells.length; i++) {
            if (cells[i] != null)
                put(grid.getColumns().get(i).getName(), cells[i]);
        }
    }

    public void setGrid(ZincGrid grid) {
        this.grid = grid;
    }

    public ZincGrid getGrid() {
        return grid;
    }

    @Override
    public boolean isEmpty() {
        if (cells.length == 0)
            return true;
        if (grid.getColumns().size() == 1 && "empty".equals(grid.getColumns().get(0).getName()))
            return true;
        return false;
    }

    //    @Override
    //    @SuppressWarnings("unchecked")
    //    public <T extends HValue> T get(String name) {
    //        ZincColumn col = grid.getColumn(name);
    //        if (col != null) {
    //            HValue val = cells[col.getIndex()];
    //            if (val != null)
    //                return (T) val;
    //        }
    //        return null;
    //    }
    //
    /**
     * Get a cell by column. If cell is null then raise
     * UnknownNameException or return null based on checked flag.
     */
    @SuppressWarnings("unchecked")
    public <T extends HValue> T get(ZincColumn col) {
        HValue val = cells[col.getIndex()];
        if (val != null)
            return (T) val;
        return null;
    }
}
