package ru.spbau.mit;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;

import static org.junit.Assert.*;

public class CollectionsTest {

    private static final int INIT_SIZE = 10;
    private static final int THRESHOLD = 5;
    private static final int SUM = 45;
    private static final int DIVISOR = 2;

    private static final Predicate<Number> IS_LOWER_THRESHOLD = new Predicate<Number>() {
        @Override
        public Boolean apply(Number b) {
            return b.intValue() < THRESHOLD;
        }
    };

    private static final Function1<Number, Double> DIVIDE_FUNC = new Function1<Number, Double>() {
        @Override
        public Double apply(Number base) {
            return (double) base.intValue() / DIVISOR;
        }
    };

    private static final Function2<Integer, Number, Integer> SUM_FUNC = new Function2<Integer, Number, Integer>() {
        @Override
        public Integer apply(Integer number, Number base) {
            return base.intValue() + number;
        }
    };

    private ArrayList<Integer> collection;

    @Before
    public void initCollection() {
        collection = new ArrayList<>();
        for (int i = 0; i < INIT_SIZE; i++) {
            collection.add(i);
        }
        assertTrue(collection.size() == INIT_SIZE);
    }

    @Test
    public void testMap() throws Exception {
        int counter = 0;
        Iterator<Integer> it = collection.iterator();
        Iterator<Double> itTemp = Collections.map(DIVIDE_FUNC, collection).iterator();
        while (it.hasNext() && itTemp.hasNext()) {
            assertTrue((double) it.next() / DIVISOR == itTemp.next());
            counter++;
        }
        assertTrue(it.hasNext() == itTemp.hasNext());
        assertTrue(counter == INIT_SIZE);
    }

    @Test
    public void testFilter() throws Exception {
        int counter = 0;
        for (Integer num : Collections.filter(IS_LOWER_THRESHOLD, collection)) {
            assertTrue(num == counter && IS_LOWER_THRESHOLD.apply(num));
            counter++;
        }
        assertTrue(counter == THRESHOLD);
    }

    @Test
    public void testTakeWhile() throws Exception {
        int counter = 0;
        for (Integer num : Collections.takeWhile(IS_LOWER_THRESHOLD, collection)) {
            assertTrue(num == counter && IS_LOWER_THRESHOLD.apply(num));
            counter++;
        }
        assertTrue(counter == THRESHOLD);
    }

    @Test
    public void testTakeUnless() throws Exception {
        int counter = 0;
        for (Integer num : Collections.takeUnless(IS_LOWER_THRESHOLD.not(), collection)) {
            assertTrue(num == counter && IS_LOWER_THRESHOLD.apply(num));
            counter++;
        }
        assertTrue(counter == THRESHOLD);
    }

    @Test
    public void testFoldl() throws Exception {
        assertTrue(Collections.foldl(SUM_FUNC, 0, collection) == SUM);
    }

    @Test
    public void testFoldr() throws Exception {
        assertTrue(Collections.foldr(SUM_FUNC, 0, collection) == SUM);
    }
}
