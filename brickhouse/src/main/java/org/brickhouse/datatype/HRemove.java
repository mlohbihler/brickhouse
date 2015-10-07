/* 
 * Copyright (c) 2015, Matthew Lohbihler
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
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
