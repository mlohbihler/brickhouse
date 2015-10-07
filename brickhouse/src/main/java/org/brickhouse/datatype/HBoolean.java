/* 
 * Copyright (c) 2015, Matthew Lohbihler
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.brickhouse.datatype;

public class HBoolean extends HValue {
    public static final HBoolean TRUE = new HBoolean(true);
    public static final HBoolean FALSE = new HBoolean(false);

    private final boolean value;

    public HBoolean(boolean value) {
        this.value = value;
    }

    public boolean isValue() {
        return value;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (value ? 1231 : 1237);
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
        HBoolean other = (HBoolean) obj;
        if (value != other.value)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return value ? "true" : "false";
    }
}
