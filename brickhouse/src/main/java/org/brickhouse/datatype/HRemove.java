package org.brickhouse.datatype;

public class HRemove extends HValue {
    public static final HRemove VALUE = new HRemove();

    private HRemove() {
        // no op
    }

    @Override
    public int hashCode() {
        return 1276383;
    }

    @Override
    public boolean equals(Object that) {
        return this == that;
    }

    @Override
    public String toString() {
        return "remove";
    }
}
