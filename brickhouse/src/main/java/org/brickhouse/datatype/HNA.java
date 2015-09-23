package org.brickhouse.datatype;

public class HNA extends HValue {
    public static final HNA VALUE = new HNA();

    private HNA() {
        // no op
    }

    @Override
    public int hashCode() {
        return 0x6e61;
    }

    @Override
    public boolean equals(Object that) {
        return this == that;
    }

    @Override
    public String toString() {
        return "na";
    }
}
