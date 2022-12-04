package com.github.pqnelson;

import java.io.Reader;
import java.util.LinkedList;
import java.util.function.IntConsumer;

/**
 * A macro to count the line numbers, and notify any observers who want
 * the information.
 */
public class LineNumberCounter implements ReaderMacro {
    /**
     * The number for the line currently being read by the Lisp Reader.
     */
    private int line = 1;
    /**
     * Any observers who want to do something when the line number increments.
     */
    private LinkedList<IntConsumer> callbacks;

    /**
     * Construct a new ReaderMacro to track the line numbers.
     */
    public LineNumberCounter() {
        this.line = 1;
        this.callbacks = new LinkedList<>();
    }

    /**
     * A getter method.
     *
     * @return The current line number for the Lisp Reader's source.
     */
    public int getLine() {
        return this.line;
    }

    /**
     * Add a callback to watch when the line number increments.
     *
     * @param callback The consumer of line numbers.
     */
    public void registerCallback(final IntConsumer callback) {
        this.callbacks.add(callback);
    }

    /**
     * With one hand we giveth, with the other we taketh away.
     *
     * @param callback The consumer who wishes to disassociate
     * themselves from our company.
     */
    public void unregisterCallback(final IntConsumer callback) {
        this.callbacks.remove(callback);
    }

    /**
     * Increment the line number, notify the observers, then ride off
     * into the sunset.
     *
     * @param stream The underlying input stream.
     * @param table The Lisp Reader invoking {@code this} reader macro.
     * @return {@code null} because this reader macro is purely for
     *         side effects.
     */
    @Override
    public Object apply(final Reader stream, final AbstractReadTable table) {
        this.line++;
        for (final IntConsumer observer : this.callbacks) {
            observer.accept(this.line);
        }
        return null;
    }
}