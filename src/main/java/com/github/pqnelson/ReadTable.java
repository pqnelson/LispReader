package com.github.pqnelson;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.io.StringReader;

import java.util.HashMap;

/**
 * A table-driven Lisp reader with reader macros.
 *
 * <p>Unlike Common Lisp, the order of interpreting a character places
 * invoking reader macros <em>before</em> skipping whitespace. This
 * permits using a reader macro to count line numbers.</p>
 *
 * @see <a href="https://www.cs.cmu.edu/Groups/AI/html/cltl/clm/node186.html#SECTION002600000000000000000">Common Lisp the Language, chapter 22</a>
 */
public class ReadTable extends AbstractReadTable {
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
     * Mapping of character [code points] to {@code ReaderMacro} instances.
     */
    private HashMap<Integer, ReaderMacro> macroBindings;

    /**
     * Create a reader for a string source.
     *
     * @param snippet The string we're lexing and parsing for data.
     */
    public ReadTable(final String snippet) {
        this(new StringReader(snippet));
    }

    /**
     * Create a Lisp-reader for a {@code Reader} object as the input source.
     *
     * @param reader The {@code java.io.Reader} input source.
     */
    public ReadTable(final Reader reader) {
        this.source = new PushbackReader(reader, BUFFERSIZE);
        this.macroBindings = new HashMap<>();
    }

    /**
     * Register a {@code ReaderMacro} to be bound to a specific character.
     *
     * <p>This will overwrite any existing binding to the given character.</p>
     *
     * @param character The UTF-16 character being bound to the reader macro.
     * @param macro The specific ReaderMacro instance we will invoke upon
     * reading a {@code character} from input.
     */
    public void addMacro(final char character, final ReaderMacro macro) {
        this.addMacro((int) character, macro);
    }


    /**
     * Register a {@code ReaderMacro} to be bound to a specific character.
     *
     * <p>This will overwrite any existing binding to the given character.</p>
     *
     * @param codepoint The value of the codepoint for the character
     * being bound to the reader macro.
     * @param macro The specific ReaderMacro instance we will invoke upon
     * reading a {@code character} from input.
     */
    public void addMacro(final int codepoint, final ReaderMacro macro) {
        this.macroBindings.put(codepoint, macro);
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
            if (this.macroBindings.containsKey(codepoint)) {
                ReaderMacro macro = this.macroBindings.get(codepoint);
                Object result = macro.apply(this.source, this);
                if (null != result) {
                    return result;
                }
            } else if (!Character.isWhitespace(codepoint)) {
                this.unread(codepoint);
                return this.buildToken();
            }
        }
    }

    private Object buildToken() {
        StringBuffer buf = new StringBuffer();
        while (!this.isFinished()) {
            final int cp = next();

            if (this.macroBindings.containsKey(cp)
                || Character.isWhitespace(cp)) {
                this.unread(cp);
                break;
            }

            buf.appendCodePoint(cp);
        }
        return buf.toString();
    }
}