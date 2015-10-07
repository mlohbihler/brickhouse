/* 
 * Copyright (c) 2015, Matthew Lohbihler
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.brickhouse.io;

import org.brickhouse.datatype.HGrid;

/**
 * HGridWriter is base class for writing grids to an output stream.
 *
 * @see <a href='http://project-haystack.org/doc/Rest#contentNegotiation'>Project Haystack</a>
 */
public interface HGridWriter {
    /** Write a grid */
    void writeGrid(HGrid grid);

    /** Flush output stream */
    void flush();

    /** Close output stream */
    void close();
}
