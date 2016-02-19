package ru.spbau.mit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class TrieImplTest {

    @org.junit.Test
    public void testAdd() throws Exception {
        StringSet trie = new StringSetImpl();

        assertTrue(trie.add("Hello"));
        assertTrue(trie.add("World"));

        assertEquals(2, trie.size());

        assertTrue(trie.contains("Hello"));
        assertTrue(trie.contains("World"));
        assertFalse(trie.contains("H"));
        assertFalse(trie.contains("Wor"));
        assertFalse(trie.contains(""));
        assertFalse(trie.contains("HelloWorld"));

        trie.add("Hi");

        assertEquals(3, trie.size());
        assertEquals(2, trie.howManyStartsWithPrefix("H"));
        assertEquals(1, trie.howManyStartsWithPrefix("W"));

        assertFalse(trie.contains("H"));
        assertFalse(trie.contains("He"));

        trie.add("");

        assertEquals(4, trie.size());
        assertEquals(4, trie.howManyStartsWithPrefix(""));
        assertTrue(trie.contains(""));
    }

    @org.junit.Test
    public void testContains() throws Exception {
        StringSet trie = new StringSetImpl();

        trie.add("Hello");
        trie.add("World");
        trie.add("Hello world!");
        trie.add("Fizz");
        trie.add("Buzz");
        trie.add("FizzBuzz");
        trie.add("C++ > Java");

        assertTrue(trie.contains("Buzz"));
        assertTrue(trie.contains("Fizz"));
        assertTrue(trie.contains("Hello"));
        assertTrue(trie.contains("World"));
        assertTrue(trie.contains("FizzBuzz"));

        trie.remove("Hello world!");
        assertFalse(trie.contains("Hello world!"));
        assertTrue(trie.contains("Hello"));
        trie.remove("Fizz");
        assertTrue(trie.contains("FizzBuzz"));
        assertFalse(trie.contains("Fizz"));
    }

    @org.junit.Test
    public void testRemove() throws Exception {
        StringSet trie = new StringSetImpl();

        trie.add("Hello");
        trie.add("World");

        assertTrue(trie.remove("Hello"));
        assertEquals(1, trie.size());

        trie.add("");
        trie.add("HI");
        trie.add("Hi");

        assertEquals(4, trie.howManyStartsWithPrefix(""));
        assertEquals(4, trie.size());
        assertEquals(2, trie.howManyStartsWithPrefix("H"));

        assertTrue(trie.remove(""));
        assertFalse(trie.remove(""));
        assertFalse(trie.remove("Hello"));
        assertTrue(trie.remove("HI"));

        assertEquals(1, trie.howManyStartsWithPrefix("H"));
    }

    @org.junit.Test
    public void testHowManyStartsWithPrefix() throws Exception {
        StringSet trie = new StringSetImpl();
        trie.add("AAB");
        trie.add("AAB");
        trie.add("AAB");
        assertEquals(1, trie.size());
        trie.add("AAC");
        assertEquals(2, trie.size());
        trie.add("AAD");
        assertEquals(3, trie.size());
        trie.add("AA");
        assertEquals(4, trie.size());
        trie.add("A");
        assertEquals(5, trie.size());
        trie.add("BBC");
        assertEquals(6, trie.size());
        trie.add("BBD");
        assertEquals(7, trie.size());
        trie.add("BBE");
        assertEquals(8, trie.size());
        trie.add("B");
        assertEquals(9, trie.size());
        assertTrue(trie.remove("A"));
        assertEquals(8, trie.size());
        assertTrue(trie.remove("B"));
        assertEquals(7, trie.size());
        assertTrue(trie.contains("AAD"));
        assertTrue(trie.contains("BBC"));
        assertTrue(trie.contains("BBD"));
        assertTrue(trie.contains("BBE"));
        assertEquals(3, trie.howManyStartsWithPrefix("BB"));
    }
}