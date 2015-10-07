/* 
 * Copyright (c) 2015, Matthew Lohbihler
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.brickhouse.json;

import org.apache.commons.lang3.StringUtils;
import org.brickhouse.datatype.HReference;
import org.brickhouse.datatype.HValue;

import com.serotonin.json.JsonException;

public class HReferenceConverter extends HValueConverter {
    public static final char CODE = 'r';

    @Override
    protected char getTypeCode() {
        return CODE;
    }

    @Override
    protected String toString(Object value) {
        HReference r = (HReference) value;

        StringBuilder sb = new StringBuilder();
        sb.append(r.getId());
        if (!StringUtils.isEmpty(r.getDis()))
            sb.append(' ').append(r.getDis());

        return sb.toString();
    }

    @Override
    protected HValue fromString(String value) throws JsonException {
        int pos = value.indexOf(' ');
        if (pos == -1)
            return new HReference(value);
        return new HReference(value.substring(0, pos), value.substring(pos + 1));
    }
}
