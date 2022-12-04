package com.github.pqnelson;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class NaiveReadTableTest {
    /**
     * The reader, given a string consisting of whitespace, should skip
     * the whitespace and then realize it is finished.
     */
    @Test
    public void emptyStringTest() {
        NaiveReadTable r = new NaiveReadTable("    \t\n     ");
        Object expected = null;
        assertEquals(expected, r.read());
        assertTrue(r.isFinished());
    }

    /**
     * The reader, given a nonempty string, should skip the whitespace
     * and read the first token. Furthermore, it should not yet be
     * finished.
     */
    @Test
    public void nonemptyStringTest() {
        NaiveReadTable r = new NaiveReadTable("    \t\n     foo   \n\n\t\n\n spam");
        Object expected = "foo";
        assertEquals(expected, r.read());
        assertFalse(r.isFinished());
    }

    /**
     * Test that the Naive read table will exhaust the stream.
     */
    @Test
    public void exhaustsInputTest() {
        NaiveReadTable r = new NaiveReadTable("    \t\n    \n\n\t\n\n  the-end");
        Object expected = "the-end";
        assertEquals(expected, r.read());
        assertTrue(r.isFinished());
    }

    @Test
    public void compositeReadTest() {
        NaiveReadTable r = new NaiveReadTable("    \t\n     foo   \n\n\t\n\n spam");
        Object expected = "foo";
        assertEquals(expected, r.read());
        assertFalse(r.isFinished());
        expected = "spam";
        assertEquals(expected, r.read());
        expected = null;
        assertEquals(expected, r.read());
        assertTrue(r.isFinished());
    }

    @Test
    public void endsWithSingleCharTest() {
        NaiveReadTable r = new NaiveReadTable("    \t\n    \n\n\t\n\n  d");
        Object expected = "d";
        assertEquals(expected, r.read());
        assertTrue(r.isFinished());
    }
}