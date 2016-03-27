package ru.spbau.mit;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;

import static org.junit.Assert.*;

public class CollectionsTest {

    private ArrayList<Integer> collection;
    private final int initSize = 10;
    private final int threshold = 5;
    private final int sum = 45;
    private final int divisor = 2;

    private Predicate<Number> predicate = new Predicate<Number>() {
        @Override
        public Boolean apply(Number b) {
            return b.intValue() < threshold;
        }
    };

    private Function1<Number, Double> func = new Function1<Number, Double>() {
        @Override
        public Double apply(Number base) {
            return (double) base.intValue() / divisor;
        }
    };

    private Function2<Integer, Number, Integer> func2 = new Function2<Integer, Number, Integer>() {
        @Override
        public Integer apply(Integer number, Number base) {
            return base.intValue() + number;
        }
    };

    @Before
    public void initCollection() {
        collection = new ArrayList<>();
        for (int i = 0; i < initSize; i++) {
            collection.add(i);
        }
        assertTrue(collection.size() == initSize);
    }

    @Test
    public void testMap() throws Exception {
        int counter = 0;
        Iterator<Integer> it = collection.iterator();
        Iterator<Double> itTemp = Collections.map(func, collection).iterator();
        while (it.hasNext() && itTemp.hasNext()) {
            assertTrue((double) it.next() / divisor == itTemp.next());
            counter++;
        }
        assertTrue(it.hasNext() == itTemp.hasNext());
        assertTrue(counter == initSize);
    }

    @Test
    public void testFilter() throws Exception {
        int counter = 0;
        for (Integer num : Collections.filter(predicate, collection)) {
            assertTrue(num == counter && predicate.apply(num));
            counter++;
        }
        assertTrue(counter == threshold);
    }

    @Test
    public void testTakeWhile() throws Exception {
        int counter = 0;
        for (Integer num : Collections.takeWhile(predicate, collection)) {
            assertTrue(num == counter && predicate.apply(num));
            counter++;
        }
        assertTrue(counter == threshold);
    }

    @Test
    public void testTakeUnless() throws Exception {
        int counter = 0;
        for (Integer num : Collections.takeUnless(predicate.not(), collection)) {
            assertTrue(num == counter && predicate.apply(num));
            counter++;
        }
        assertTrue(counter == threshold);
    }

    @Test
    public void testFoldl() throws Exception {
        assertTrue(Collections.foldl(func2, 0, collection) == sum);
    }

    @Test
    public void testFoldr() throws Exception {
        assertTrue(Collections.foldr(func2, 0, collection) == sum);
    }
}
