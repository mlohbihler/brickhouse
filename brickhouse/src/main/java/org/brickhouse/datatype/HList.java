package org.brickhouse.datatype;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class HList extends HValue {
    private final List<HValue> list = new LinkedList<>();

    public HList() {
        // no op
    }

    public <T extends HValue> HList(List<T> list) {
        this.list.addAll(list);
    }

    public <T extends HValue> HList(Set<T> set) {
        this.list.addAll(set);
    }

    public HList(HValue... values) {
        for (HValue value : values)
            list.add(value);
    }

    public HList(HList list) {
        this.list.addAll(list.list);
    }

    public HList addAll(HList that) {
        list.addAll(that.getList());
        return this;
    }

    public HList addStrings(List<String> strings) {
        for (String s : strings)
            add(new HString(s));
        return this;
    }

    public HList add(String value) {
        list.add(new HString(value));
        return this;
    }

    public HList add(boolean b) {
        list.add(b ? HBoolean.TRUE : HBoolean.FALSE);
        return this;
    }

    public HList add(HValue value) {
        list.add(value);
        return this;
    }

    public HList add(int value) {
        list.add(new HNumber(value));
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T extends HValue> T get(int index) {
        return (T) list.get(index);
    }

    public HList delete(HValue value) {
        list.remove(value);
        return this;
    }

    public HList delete(int index) {
        list.remove(index);
        return this;
    }

    public int size() {
        return list.size();
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public List<HValue> getList() {
        return list;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((list == null) ? 0 : list.hashCode());
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
        HList other = (HList) obj;
        if (list == null) {
            if (other.list != null)
                return false;
        }
        else if (!list.equals(other.list))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return list.toString();
    }
}
