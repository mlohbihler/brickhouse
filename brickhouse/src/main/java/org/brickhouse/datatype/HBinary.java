package org.brickhouse.datatype;

public class HBinary extends HValue {
    private final String mime;

    public HBinary(String mime) {
        if (mime == null || mime.length() == 0 || mime.indexOf('/') < 0)
            throw new IllegalArgumentException("Invalid mime val: \"" + mime + "\"");
        for (int i = 0; i < mime.length(); i++) {
            int c = mime.charAt(i);
            if (c > 127 || c == ')')
                throw new IllegalArgumentException("Invalid mime, char='" + (char) c + "'");
        }

        this.mime = mime;
    }

    public String getMime() {
        return mime;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((mime == null) ? 0 : mime.hashCode());
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
        HBinary other = (HBinary) obj;
        if (mime == null) {
            if (other.mime != null)
                return false;
        }
        else if (!mime.equals(other.mime))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Bin(" + mime + ")";
    }
}
