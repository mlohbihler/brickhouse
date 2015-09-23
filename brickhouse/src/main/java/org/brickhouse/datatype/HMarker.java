package org.brickhouse.datatype;

public class HMarker extends HValue {
    public static final HMarker VALUE = new HMarker();

    private HMarker() {
        // no op
    }

    @Override
    public int hashCode() {
        return 1276382;
    }

    @Override
    public boolean equals(Object that) {
        return this == that;
    }

    @Override
    public String toString() {
        return "marker";
    }
}
