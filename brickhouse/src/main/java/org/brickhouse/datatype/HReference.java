package org.brickhouse.datatype;

import java.util.UUID;

public class HReference extends HValue {
    private static boolean idChars[];
    static {
        idChars = new boolean[127];

        for (int i = 97; i <= 122; i++)
            idChars[i] = true;
        for (int i = 65; i <= 90; i++)
            idChars[i] = true;
        for (int i = 48; i <= 57; i++)
            idChars[i] = true;
        idChars[95] = true;
        idChars[58] = true;
        idChars[45] = true;
        idChars[46] = true;
        idChars[126] = true;
    }

    public static final HReference nullRef = new HReference("null", null);

    private final String id;
    private final String dis;

    public HReference() {
        id = UUID.randomUUID().toString();
        dis = null;
    }

    public HReference(String id) {
        this(id, null);
    }

    public HReference(String id, String dis) {
        if (!isId(id))
            throw new IllegalArgumentException("Invalid id val: \"" + id + "\"");

        this.id = id;
        this.dis = dis;
    }

    public String getDis() {
        return dis;
    }

    public String getId() {
        return id;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
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
        HReference other = (HReference) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        }
        else if (!id.equals(other.id))
            return false;
        return true;
    }

    public static boolean isId(String id) {
        if (id == null)
            return false;
        if (id.length() == 0)
            return false;
        for (int i = 0; i < id.length(); i++)
            if (!isIdChar(id.charAt(i)))
                return false;
        return true;
    }

    public static boolean isIdChar(int ch) {
        return ch >= 0 && ch < idChars.length && idChars[ch];
    }

    @Override
    public String toString() {
        if (dis == null)
            return id;
        return id + " " + dis;
    }
}
