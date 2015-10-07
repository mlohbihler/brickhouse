/* 
 * Copyright (c) 2015, Matthew Lohbihler
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.brickhouse.json;

import org.brickhouse.datatype.HDate;
import org.brickhouse.datatype.HValue;

public class HDateConverter extends HValueConverter {
    public static final char CODE = 'd';

    @Override
    protected char getTypeCode() {
        return CODE;
    }

    @Override
    protected String toString(Object value) {
        return value.toString();
    }

    @Override
    protected HValue fromString(String value) {
        return new HDate(value);
    }
}
