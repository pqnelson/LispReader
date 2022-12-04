package com.github.pqnelson;

import java.io.Reader;
import java.util.ArrayList;

/**
 * Collect values read until a delimiting token is encountered, then
 * return the collected values as an {@code ArrayList}.
 */
public class AccumulatorReaderMacro implements ReaderMacro {
    /**
     * The "needle" we're looking for.
     */
    private final String stopToken;

    /**
     * Construct an accumulator reader macro, using the specific
     * character as a standalone stopping token.
     *
     * <p>Assumes the character is already recognized by the Lisp Reader
     * as a one-character token.</p>
     *
     * @param character The single-char stopping delimiter.
     */
    public AccumulatorReaderMacro(final char character) {
        this("" + character);
    }

    /**
     * Construct an accumulator reader macro, using the specific
     * stopping token is encountered.
     *
     * @param delimiter The "needle" in the haystack.
     */
    public AccumulatorReaderMacro(final String delimiter) {
        this.stopToken = delimiter;
    }

    /**
     * Accumulate a collection of values until the stopping token is
     * read, then return the {@code List} of values.
     *
     * @param stream The underlying input stream.
     * @param table The Lisp Reader invoking {@code this} reader macro.
     * @return {@code null} if the table is finished, otherwise it returns
     *         an {@code ArrayList} of values from {@code table.read()}.
     */
    @Override
    public Object apply(final Reader stream, final AbstractReadTable table) {
        if (table.isFinished()) {
            return null;
        }

        ArrayList<Object> coll = new ArrayList<>();
        Object entry;
        while (!table.isFinished()) {
            entry = table.read();
            if (this.stopToken.equals(entry)) {
                break;
            } else {
                coll.add(entry);
            }
        }

        return coll;
    }
}