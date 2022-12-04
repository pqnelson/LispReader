package com.github.pqnelson;

import java.io.Reader;

/**
 * A functional interface encapsulating the essence of a "Lisp Reader Macro".
 */
interface ReaderMacro {
    /**
     * Apply the reader macro, using the given {@code stream}
     * and {@code table}.
     *
     * <p>We assume the stream is <em>read</em> but not replaced, or
     * stuffed with characters (e.g., a macro which just inserts a
     * string to the top of the stream). But {@code stream.unread()} may
     * occur, for example, to push a character just read out of the
     * stream back into it. We trust the programmer to Do The Right
     * Thing&trade;.</p>
     *
     * @param stream The underlying source of characters.
     * @param table The LispReader which invoked the reader macro.
     * @return A Lisp object formed from the reader macro, or {@code null}
     * if the reader macro was used just for side effects.
     */
    Object apply(Reader stream, AbstractReadTable table);
}