package org.brickhouse.datatype;

public class HString extends HValue implements Comparable<HString> {
    public static final HString EMPTY = new HString("");

    private final String value;

    public HString(String value) {
        if (value == null)
            throw new NullPointerException();
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((value == null) ? 0 : value.hashCode());
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
        HString other = (HString) obj;
        if (value == null) {
            if (other.value != null)
                return false;
        }
        else if (!value.equals(other.value))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public int compareTo(HString that) {
        return value.compareTo(that.value);
    }
}
