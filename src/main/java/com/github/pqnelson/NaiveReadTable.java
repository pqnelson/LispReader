package com.github.pqnelson;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.io.StringReader;

/**
 * A table-driven Lisp reader without any reader macros.
 */
public class NaiveReadTable extends AbstractReadTable {
    /**
     * The source of characters to be read as Lisp data.
     */
    private final PushbackReader source;

    /**
     * Are we finished reading yet?
     */
    private boolean finished = false;

    /**
     * The size of the buffer for the push back reader {@code source}.
     * The default PushbackReader buffer size is too small to actually
     * be useful, and I thought 50 was a nice round number.
     */
    private static final int BUFFERSIZE = 50; // "Why not 35 or 39?" ;p

    /**
     * Create a reader for a string source.
     *
     * @param snippet The string we're lexing and parsing for data.
     */
    public NaiveReadTable(final String snippet) {
        this(new StringReader(snippet));
    }

    /**
     * Create a Lisp-reader for a {@code Reader} object as the input source.
     *
     * @param reader The {@code java.io.Reader} input source.
     */
    public NaiveReadTable(final Reader reader) {
        this.source = new PushbackReader(reader, BUFFERSIZE);
    }

    /**
     * Next character in the input stream.
     *
     * @return The code point for the next character in the input stream,
     * and {@code -1} if there's nothing left to read.
     */
    private int next() {
        try {
            int codepoint = this.source.read();
            if (-1 == codepoint) {
                this.finished = true;
            }
            return codepoint;
        } catch (IOException e) {
            finished = true;
            return -1;
        }
    }

    /**
     * Put the specific code point back into the input stream.
     *
     * @param c The code point for the character.
     */
    private void unread(final int c) {
        try {
            this.source.unread(c);
        } catch (IOException e) {
        }
    }

    /**
     * Test if the input stream is exhausted.
     *
     * @return True if the reader is exhausted.
     */
    @Override
    public boolean isFinished() {
        if (!this.finished) {
            int peek = next();
            if (-1 == peek) {
                this.finished = true;
            } else {
                unread(peek);
            }
        }
        return this.finished;
    }


    /**
     * Read Lisp data from the given input stream.
     *
     * @return New Lisp data, unless the input has been exhausted.
     */
    @Override
    public Object read() {
        while (true) {
            if (this.isFinished()) {
                return null;
            }
            final int codepoint = this.next();
            if (!Character.isWhitespace(codepoint)) {
                this.unread(codepoint);
                return this.buildToken();
            }
        }
    }

    private Object buildToken() {
        StringBuffer buf = new StringBuffer();
        int cp = next();
        while (!Character.isWhitespace(cp)) {
            buf.appendCodePoint(cp);
            if (this.isFinished()) {
                break;
            } else {
                cp = next();
            }
        }
        return buf.toString();
    }
}