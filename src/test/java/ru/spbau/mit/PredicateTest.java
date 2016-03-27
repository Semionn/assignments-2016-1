package ru.spbau.mit;

import org.junit.Test;

import static org.junit.Assert.*;

public class PredicateTest {

    private final int checkNum = 42;
    private final int checkNum2 = 0;

    private Predicate<Integer> predicate = new Predicate<Integer>() {
        @Override
        public Boolean apply(Integer num) {
            return num == checkNum;
        }
    };

    private Predicate<Integer> predicate2 = new Predicate<Integer>() {
        @Override
        public Boolean apply(Integer num) {
            return num > checkNum2;
        }
    };

    private Predicate<Integer> predicateNull = new Predicate<Integer>() {
        @Override
        public Boolean apply(Integer num) {
            return num == null;
        }
    };

    @Test
    public void testApply() throws Exception {
        assertTrue(predicate.apply(checkNum));
    }

    @Test
    public void testOr() throws Exception {
        assertTrue(predicate.or(predicate2).apply(1 + checkNum));
        assertFalse(predicate.or(predicate2).apply(checkNum2 - 1));
        assertTrue(predicateNull.or(predicate).apply(null));
    }

    @Test
    public void testAnd() throws Exception {
        assertTrue(predicate.and(predicate2).apply(checkNum));
        assertFalse(predicate.and(predicate2).apply(-checkNum));
        assertFalse(predicateNull.not().and(predicate).apply(null));
    }

    @Test
    public void testNot() throws Exception {
        assertFalse(predicate.not().apply(checkNum));
    }

    @Test
    public void testCompose() throws Exception {
        Function1<Boolean, Integer> func = new Function1<Boolean, Integer>() {
            @Override
            public Integer apply(Boolean b) {
                if (b) {
                    return 1;
                }
                return 0;
            }
        };

        assertTrue(predicate.compose(func).apply(checkNum) == 1);
        assertTrue(predicate.compose(func).apply(-checkNum) == 0);
    }

    @Test
    public void testConstantPredicates() {
        assertTrue(Predicate.ALWAYS_TRUE.apply(null));
        assertFalse(Predicate.ALWAYS_FALSE.apply(null));
    }
}
