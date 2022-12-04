package com.github.pqnelson;

/**
 * Since we are exploring the "space" of Lisp readers, it helps to have
 * an abstract base class to use when writing the {@code ReaderMacro} interface.
 */
public abstract class AbstractReadTable {
    /**
     * Check if there's anything left to read.
     *
     * @return True if the underlying input is exhausted.
     */
    public abstract boolean isFinished();
    /**
     * Read Lisp data from some underlying input stream.
     *
     * @return The Lisp data as a Java object.
     */
    public abstract Object read();
}