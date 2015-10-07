/* 
 * Copyright (c) 2015, Matthew Lohbihler
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.brickhouse.filter;

import java.util.ArrayList;
import java.util.List;

import org.brickhouse.ParseException;
import org.brickhouse.datatype.HMap;
import org.brickhouse.datatype.HReference;
import org.brickhouse.datatype.HString;
import org.brickhouse.datatype.HValue;
import org.brickhouse.zinc.ZincReader;

/**
 * HFilter models a parsed tag query string.
 * 
 * @see <a href='http://project-haystack.org/doc/Filters'>Project Haystack</a>
 */
public abstract class Filter {

    //////////////////////////////////////////////////////////////////////////
    // Encoding
    //////////////////////////////////////////////////////////////////////////

    public static Filter parse(String s) {
        try {
            return new ZincReader(s).readFilter();
        }
        catch (Exception e) {
            if (e instanceof ParseException)
                throw e;
            throw new ParseException(s, e);
        }
    }

    //////////////////////////////////////////////////////////////////////////
    // Factories
    //////////////////////////////////////////////////////////////////////////

    /**
     * Match records which have the specified tag path defined.
     */
    public static Filter has(String path) {
        return new Has(Path.make(path));
    }

    /**
     * Match records which do not define the specified tag path.
     */
    public static Filter missing(String path) {
        return new Missing(Path.make(path));
    }

    /**
     * Match records which have a tag are equal to the specified value.
     * If the path is not defined then it is unmatched.
     */
    public static Filter eq(String path, String val) {
        return new Eq(Path.make(path), new HString(val));
    }

    public static Filter eq(String path, HValue val) {
        return new Eq(Path.make(path), val);
    }

    /**
     * Match records which have a tag not equal to the specified value.
     * If the path is not defined then it is unmatched.
     */
    public static Filter ne(String path, String val) {
        return new Ne(Path.make(path), new HString(val));
    }

    public static Filter ne(String path, HValue val) {
        return new Ne(Path.make(path), val);
    }

    /**
     * Match records which have tags less than the specified value.
     * If the path is not defined then it is unmatched.
     */
    public static Filter lt(String path, HValue val) {
        return new Lt(Path.make(path), val);
    }

    /**
     * Match records which have tags less than or equals to specified value.
     * If the path is not defined then it is unmatched.
     */
    public static Filter le(String path, HValue val) {
        return new Le(Path.make(path), val);
    }

    /**
     * Match records which have tags greater than specified value.
     * If the path is not defined then it is unmatched.
     */
    public static Filter gt(String path, HValue val) {
        return new Gt(Path.make(path), val);
    }

    /**
     * Match records which have tags greater than or equal to specified value.
     * If the path is not defined then it is unmatched.
     */
    public static Filter ge(String path, HValue val) {
        return new Ge(Path.make(path), val);
    }

    /**
     * Match records which have tags greater than or equal to specified value.
     * If the path is not defined then it is unmatched.
     */
    public static Filter like(String path, HValue val) {
        return new Like(Path.make(path), val);
    }

    /**
     * Match records which have tags greater than or equal to specified value.
     * If the path is not defined then it is unmatched.
     */
    public static Filter ilike(String path, HValue val) {
        return new ILike(Path.make(path), val);
    }

    /**
     * Return a query which is the logical-and of this and that query.
     */
    public Filter and(Filter that) {
        return new And(this, that);
    }

    /**
     * Return a query which is the logical-or of this and that query.
     */
    public Filter or(Filter that) {
        return new Or(this, that);
    }

    /**
     * Return a query which is the logical-or of this and that query.
     */
    public static Filter negate(Filter that) {
        return new Negate(that);
    }

    //////////////////////////////////////////////////////////////////////////
    // Constructor
    //////////////////////////////////////////////////////////////////////////

    /** Package private constructor subclasses */
    Filter() {
    }

    //////////////////////////////////////////////////////////////////////////
    // Access
    //////////////////////////////////////////////////////////////////////////

    /* Return if given tags entity matches this query. */
    public abstract boolean include(HMap map, Pather pather);

    /** String encoding */
    @Override
    public final String toString() {
        if (string == null)
            string = toStr();
        return string;
    }

    private String string;

    /* Used to lazily build toString */
    abstract String toStr();

    /** Hash code is based on string encoding */
    @Override
    public final int hashCode() {
        return toString().hashCode();
    }

    /** Equality is based on string encoding */
    @Override
    public final boolean equals(Object that) {
        if (!(that instanceof Filter))
            return false;
        return toString().equals(that.toString());
    }

    //////////////////////////////////////////////////////////////////////////
    // HFilter.Path
    //////////////////////////////////////////////////////////////////////////

    /** Pather is a callback interface used to resolve query paths. */
    public interface Pather {
        /**
         * Given a HReference string identifier, resolve to an entity's
         * HMap respresentation or ref is not found return null.
         */
        public HMap find(String ref);
    }

    //////////////////////////////////////////////////////////////////////////
    // HFilter.Path
    //////////////////////////////////////////////////////////////////////////

    /** Path is a simple name or a complex path using the "->" separator */
    static abstract class Path {
        /** Construct a new Path from string or throw ParseException */
        public static Path make(String path) {
            try {
                // optimize for common single name case
                int dash = path.indexOf('-');
                if (dash < 0)
                    return new Path1(path);

                // parse
                int s = 0;
                List<String> acc = new ArrayList<>();
                while (true) {
                    String n = path.substring(s, dash);
                    if (n.length() == 0)
                        throw new Exception();
                    acc.add(n);
                    if (path.charAt(dash + 1) != '>')
                        throw new Exception();
                    s = dash + 2;
                    dash = path.indexOf('-', s);
                    if (dash < 0) {
                        n = path.substring(s, path.length());
                        if (n.length() == 0)
                            throw new Exception();
                        acc.add(n);
                        break;
                    }
                }
                return new PathN(path, acc.toArray(new String[acc.size()]));
            }
            catch (Exception e) {
                // no op
            }
            throw new ParseException("Path: " + path);
        }

        /** Number of names in the path. */
        public abstract int size();

        /** Get name at given index. */
        public abstract String get(int i);

        /** Hashcode is based on string. */
        @Override
        public int hashCode() {
            return toString().hashCode();
        }

        /** Equality is based on string. */
        @Override
        public boolean equals(Object that) {
            return toString().equals(that.toString());
        }

        /** Get string encoding. */
        @Override
        public abstract String toString();
    }

    static final class Path1 extends Path {
        Path1(String n) {
            this.name = n;
        }

        @Override
        public int size() {
            return 1;
        }

        @Override
        public String get(int i) {
            if (i == 0)
                return name;
            throw new IndexOutOfBoundsException("" + i);
        }

        @Override
        public String toString() {
            return name;
        }

        private final String name;
    }

    static final class PathN extends Path {
        PathN(String s, String[] n) {
            this.string = s;
            this.names = n;
        }

        @Override
        public int size() {
            return names.length;
        }

        @Override
        public String get(int i) {
            return names[i];
        }

        @Override
        public String toString() {
            return string;
        }

        private final String string;
        private final String[] names;
    }

    //////////////////////////////////////////////////////////////////////////
    // All
    //////////////////////////////////////////////////////////////////////////
    public static final All ALL = new All();

    static class All extends Filter {
        @Override
        public boolean include(HMap map, Pather pather) {
            return true;
        }

        @Override
        String toStr() {
            return "*";
        }
    }

    //////////////////////////////////////////////////////////////////////////
    // PathFilter
    //////////////////////////////////////////////////////////////////////////

    static abstract class PathFilter extends Filter {
        PathFilter(Path p) {
            path = p;
        }

        @Override
        public final boolean include(HMap map, Pather pather) {
            HValue val = map.get(path.get(0));
            if (path.size() != 1) {
                HMap nt = map;
                for (int i = 1; i < path.size(); ++i) {
                    if (!(val instanceof HReference)) {
                        val = null;
                        break;
                    }
                    nt = pather.find(((HReference) val).getId());
                    if (nt == null) {
                        val = null;
                        break;
                    }
                    val = nt.get(path.get(i));
                }
            }
            return doInclude(val);
        }

        abstract boolean doInclude(HValue val);

        final Path path;
    }

    //////////////////////////////////////////////////////////////////////////
    // Has
    //////////////////////////////////////////////////////////////////////////

    static final class Has extends PathFilter {
        Has(Path p) {
            super(p);
        }

        @Override
        final boolean doInclude(HValue v) {
            return v != null;
        }

        @Override
        final String toStr() {
            return path.toString();
        }
    }

    //////////////////////////////////////////////////////////////////////////
    // Missing
    //////////////////////////////////////////////////////////////////////////

    static final class Missing extends PathFilter {
        Missing(Path p) {
            super(p);
        }

        @Override
        final boolean doInclude(HValue v) {
            return v == null;
        }

        @Override
        final String toStr() {
            return "not " + path;
        }
    }

    //////////////////////////////////////////////////////////////////////////
    // CmpFilter
    //////////////////////////////////////////////////////////////////////////

    static abstract class CmpFilter extends PathFilter {
        CmpFilter(Path p, HValue val) {
            super(p);
            this.val = val;
        }

        @Override
        final String toStr() {
            StringBuilder s = new StringBuilder();
            s.append(path).append(cmpStr()).append(val.toString());
            return s.toString();
        }

        final boolean sameType(HValue v) {
            return v != null && v.getClass() == val.getClass();
        }

        final boolean comparable(HValue v) {
            if (!sameType(v))
                return false;
            return v instanceof Comparable && val instanceof Comparable;
        }

        @SuppressWarnings("unchecked")
        <T> int compareTo(Comparable<T> v) {
            return v.compareTo((T) val);
        }

        abstract String cmpStr();

        final HValue val;
    }

    //////////////////////////////////////////////////////////////////////////
    // Eq
    //////////////////////////////////////////////////////////////////////////

    static final class Eq extends CmpFilter {
        Eq(Path p, HValue v) {
            super(p, v);
        }

        @Override
        final String cmpStr() {
            return "==";
        }

        @Override
        final boolean doInclude(HValue v) {
            return v != null && v.equals(val);
        }
    }

    //////////////////////////////////////////////////////////////////////////
    // Ne
    //////////////////////////////////////////////////////////////////////////

    static final class Ne extends CmpFilter {
        Ne(Path p, HValue v) {
            super(p, v);
        }

        @Override
        final String cmpStr() {
            return "!=";
        }

        @Override
        final boolean doInclude(HValue v) {
            return v != null && !v.equals(val);
        }
    }

    //////////////////////////////////////////////////////////////////////////
    // Lt
    //////////////////////////////////////////////////////////////////////////

    static final class Lt extends CmpFilter {
        Lt(Path p, HValue v) {
            super(p, v);
        }

        @Override
        final String cmpStr() {
            return "<";
        }

        @Override
        final boolean doInclude(HValue v) {
            return sameType(v) && comparable(v) && compareTo((Comparable<?>) v) < 0;
        }
    }

    //////////////////////////////////////////////////////////////////////////
    // Le
    //////////////////////////////////////////////////////////////////////////

    static final class Le extends CmpFilter {
        Le(Path p, HValue v) {
            super(p, v);
        }

        @Override
        final String cmpStr() {
            return "<=";
        }

        @Override
        final boolean doInclude(HValue v) {
            return sameType(v) && comparable(v) && compareTo((Comparable<?>) v) <= 0;
        }
    }

    //////////////////////////////////////////////////////////////////////////
    // Gt
    //////////////////////////////////////////////////////////////////////////

    static final class Gt extends CmpFilter {
        Gt(Path p, HValue v) {
            super(p, v);
        }

        @Override
        final String cmpStr() {
            return ">";
        }

        @Override
        final boolean doInclude(HValue v) {
            return sameType(v) && comparable(v) && compareTo((Comparable<?>) v) > 0;
        }
    }

    //////////////////////////////////////////////////////////////////////////
    // Ge
    //////////////////////////////////////////////////////////////////////////

    static final class Ge extends CmpFilter {
        Ge(Path p, HValue v) {
            super(p, v);
        }

        @Override
        final String cmpStr() {
            return ">=";
        }

        @Override
        final boolean doInclude(HValue v) {
            return sameType(v) && comparable(v) && compareTo((Comparable<?>) v) >= 0;
        }
    }

    static class Like extends CmpFilter {
        protected boolean starting;
        protected boolean ending;
        protected String lit;

        Like(Path p, HValue v) {
            super(p, v);
            if (!(v instanceof HString))
                throw new ParseException("~ (like) operator only supports strings");

            lit = ((HString) v).getValue();

            if (lit.startsWith("%")) {
                lit = lit.substring(1);
                starting = true;
            }

            if (lit.endsWith("%")) {
                lit = lit.substring(0, lit.length() - 1);
                if (!lit.endsWith("%"))
                    ending = true;
            }

            if (!starting && !ending)
                throw new ParseException("~ (like) operator must contain a starting or ending '%'");
            if (lit.length() == 0)
                throw new ParseException("~ (like) operator must contain a string expression");
        }

        @Override
        String cmpStr() {
            return "~";
        }

        @Override
        final boolean doInclude(HValue v) {
            if (!sameType(v))
                return false;
            return compare(((HString) v).getValue());
        }

        boolean compare(String s) {
            if (starting && ending)
                return s.contains(lit);
            if (starting)
                return s.endsWith(lit);
            if (ending)
                return s.startsWith(lit);
            return false;
        }
    }

    static final class ILike extends Like {
        ILike(Path p, HValue v) {
            super(p, v);
            lit = lit.toUpperCase();
        }

        @Override
        String cmpStr() {
            return "~~";
        }

        @Override
        boolean compare(String s) {
            return super.compare(s.toUpperCase());
        }
    }

    //////////////////////////////////////////////////////////////////////////
    // Compound
    //////////////////////////////////////////////////////////////////////////

    static abstract class CompoundFilter extends Filter {
        CompoundFilter(Filter a, Filter b) {
            this.a = a;
            this.b = b;
        }

        abstract String keyword();

        @Override
        final String toStr() {
            StringBuilder s = new StringBuilder();
            if (a instanceof CompoundFilter)
                s.append('(').append(a).append(')');
            else
                s.append(a);
            s.append(' ').append(keyword()).append(' ');
            if (b instanceof CompoundFilter)
                s.append('(').append(b).append(')');
            else
                s.append(b);
            return s.toString();
        }

        final Filter a;
        final Filter b;
    }

    //////////////////////////////////////////////////////////////////////////
    // And
    //////////////////////////////////////////////////////////////////////////

    static final class And extends CompoundFilter {
        And(Filter a, Filter b) {
            super(a, b);
        }

        @Override
        final String keyword() {
            return "and";
        }

        @Override
        public final boolean include(HMap map, Pather pather) {
            return a.include(map, pather) && b.include(map, pather);
        }
    }

    //////////////////////////////////////////////////////////////////////////
    // Or
    //////////////////////////////////////////////////////////////////////////

    static final class Or extends CompoundFilter {
        Or(Filter a, Filter b) {
            super(a, b);
        }

        @Override
        final String keyword() {
            return "or";
        }

        @Override
        public final boolean include(HMap map, Pather pather) {
            return a.include(map, pather) || b.include(map, pather);
        }
    }

    //////////////////////////////////////////////////////////////////////////
    // Negate
    //////////////////////////////////////////////////////////////////////////

    static final class Negate extends Filter {
        Filter f;

        Negate(Filter f) {
            this.f = f;
        }

        @Override
        public boolean include(HMap map, Pather pather) {
            return !f.include(map, pather);
        }

        @Override
        String toStr() {
            StringBuilder s = new StringBuilder();
            s.append("!(").append(f).append(')');
            return s.toString();
        }
    }
}