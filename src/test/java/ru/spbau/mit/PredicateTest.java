package ru.spbau.mit;

import org.junit.Test;

import static org.junit.Assert.*;

public class PredicateTest {

    private static final int CHECK_NUM_1 = 42;
    private static final int CHECK_NUM_2 = 0;

    private static final Predicate<Integer> CHECK_EQUALS_NUM1 = new Predicate<Integer>() {
        @Override
        public Boolean apply(Integer num) {
            return num == CHECK_NUM_1;
        }
    };

    private static final Predicate<Integer> CHECK_EQUALS_NUM2 = new Predicate<Integer>() {
        @Override
        public Boolean apply(Integer num) {
            return num > CHECK_NUM_2;
        }
    };

    private static final Predicate<Object> CHECK_LAZY_PREDICATE = new Predicate<Object>() {
        @Override
        public Boolean apply(Object obj) {
            fail("Lazy check failed");
            return false;
        }
    };

    @Test
    public void testApply() throws Exception {
        assertTrue(CHECK_EQUALS_NUM1.apply(CHECK_NUM_1));
    }

    @Test
    public void testOr() throws Exception {
        assertTrue(CHECK_EQUALS_NUM1.or(CHECK_EQUALS_NUM2).apply(1 + CHECK_NUM_1));
        assertFalse(CHECK_EQUALS_NUM1.or(CHECK_EQUALS_NUM2).apply(CHECK_NUM_2 - 1));
        assertTrue(CHECK_EQUALS_NUM1.or(CHECK_LAZY_PREDICATE).apply(CHECK_NUM_1));
    }

    @Test
    public void testAnd() throws Exception {
        assertTrue(CHECK_EQUALS_NUM1.and(CHECK_EQUALS_NUM2).apply(CHECK_NUM_1));
        assertFalse(CHECK_EQUALS_NUM1.and(CHECK_EQUALS_NUM2).apply(-CHECK_NUM_1));
        assertFalse(CHECK_EQUALS_NUM1.not().and(CHECK_LAZY_PREDICATE).apply(CHECK_NUM_1));
    }

    @Test
    public void testNot() throws Exception {
        assertFalse(CHECK_EQUALS_NUM1.not().apply(CHECK_NUM_1));
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

        assertTrue(CHECK_EQUALS_NUM1.compose(func).apply(CHECK_NUM_1) == 1);
        assertTrue(CHECK_EQUALS_NUM1.compose(func).apply(-CHECK_NUM_1) == 0);
    }

    @Test
    public void testConstantPredicates() {
        assertTrue(Predicate.ALWAYS_TRUE.apply(null));
        assertFalse(Predicate.ALWAYS_FALSE.apply(null));
    }
}
