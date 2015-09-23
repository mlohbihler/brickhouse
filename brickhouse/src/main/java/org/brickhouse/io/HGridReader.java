package org.brickhouse.io;

import org.brickhouse.datatype.HGrid;

/**
 * HGridReader is base class for reading grids from an input stream.
 *
 * @see <a href='http://project-haystack.org/doc/Rest#contentNegotiation'>Project Haystack</a>
 */
public interface HGridReader {
    /** Read a grid */
    HGrid readGrid();
}