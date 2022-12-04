package com.github.pqnelson;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class ReadTableTest {
    /**
     * The reader, given a string consisting of whitespace, should skip
     * the whitespace and then realize it is finished.
     */
    @Test
    public void emptyStringTest() {
        ReadTable r = new ReadTable("    \t\n     ");
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
        ReadTable r = new ReadTable("    \t\n     foo   \n\n\t\n\n spam");
        Object expected = "foo";
        assertEquals(expected, r.read());
        assertFalse(r.isFinished());
    }

    /**
     * Test that the  read table will exhaust the stream.
     */
    @Test
    public void exhaustsInputTest() {
        ReadTable r = new ReadTable("    \t\n    \n\n\t\n\n  the-end");
        Object expected = "the-end";
        assertEquals(expected, r.read());
        assertTrue(r.isFinished());
    }

    @Test
    public void compositeReadTest() {
        ReadTable r = new ReadTable("    \t\n     foo   \n\n\t\n\n spam");
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
        ReadTable r = new ReadTable("    \t\n    \n\n\t\n\n  d");
        Object expected = "d";
        assertEquals(expected, r.read());
        assertTrue(r.isFinished());
    }

    @Test
    public void newlineMacroReaderTest() {
        ReadTable r = new ReadTable("    \t\n    \n\n\t\n\n  d");
        LineNumberCounter counter = new LineNumberCounter();
        r.addMacro('\n', counter);
        r.read();
        int expected = 6;
        assertEquals(expected, counter.getLine());
    }
    @Test
    public void nestedNestedListTest() {
        ReadTable r = new ReadTable("(foo (eggs (scrambed (stuff) suggests) but) and spam)");
        r.addMacro(')', new SingleCharReaderMacro(")"));
        r.addMacro('(', new AccumulatorReaderMacro(")"));

        ArrayList<Object> expected = new ArrayList<>();
        ArrayList<Object> tmp = new ArrayList<>();
        ArrayList<Object> inner = new ArrayList<>();
        inner.add("stuff");
        tmp.add("scrambed");
        tmp.add(inner);
        tmp.add("suggests");
        inner = tmp;
        tmp = new ArrayList<>();
        tmp.add("eggs");
        tmp.add(inner);
        tmp.add("but");
        expected = new ArrayList<>();
        expected.add("foo");
        expected.add(tmp);
        expected.add("and");
        expected.add("spam");
        assertEquals(expected, r.read());
    }
}