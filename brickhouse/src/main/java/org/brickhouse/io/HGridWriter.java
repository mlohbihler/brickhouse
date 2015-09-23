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
