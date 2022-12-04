package com.github.pqnelson;

import java.io.Reader;

/**
 * Register a single character as a self-contained token.
 */
public class SingleCharReaderMacro implements ReaderMacro {
    /**
     * Speak of the devil, and up he shall arise.
     */
    private final String token;

    /**
     * Register the character as a self-contained token.
     *
     * <p><b>Warning:</b> we assume the Lisp Reader is binding the
     * {@code character} to the current Reader Macro.</p>
     *
     * @param character The needle in the haystack.
     */
    public SingleCharReaderMacro(final char character) {
        this("" + character);
    }

    /**
     * Register the character as a self-contained token.
     *
     * <p><b>Warning:</b> we assume the Lisp Reader is binding the
     * {@code character} to the current Reader Macro.</p>
     *
     * @param character The needle in the haystack.
     */
    public SingleCharReaderMacro(final String character) {
        this.token = character;
    }

    /**
     * Returns the token when encountered.
     *
     * @param stream The underlying input stream.
     * @param table The Lisp Reader invoking {@code this} reader macro.
     * @return The character being treated as a token.
     */
    @Override
    public Object apply(final Reader stream, final AbstractReadTable table) {
        return this.token;
    }
}