package org.brickhouse.zinc;

import org.brickhouse.datatype.HMap;
import org.brickhouse.datatype.HString;
import org.brickhouse.datatype.HValue;

public class ZincColumn {
    private final int index;
    private final String name;
    private final HMap meta;

    public ZincColumn(int index, String name, HMap meta) {
        this.index = index;
        this.name = name;
        this.meta = meta == null ? HMap.EMPTY : meta;
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public HMap getMeta() {
        return meta;
    }

    public String dis() {
        HValue dis = meta.get("dis");
        if (dis instanceof HString)
            return ((HString) dis).getValue();
        return name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((meta == null) ? 0 : meta.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
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
        ZincColumn other = (ZincColumn) obj;
        if (meta == null) {
            if (other.meta != null)
                return false;
        }
        else if (!meta.equals(other.meta))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        }
        else if (!name.equals(other.name))
            return false;
        return true;
    }
}
