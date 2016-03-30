package ru.spbau.mit;

import org.junit.Test;

import static org.junit.Assert.*;

public class Function1Test {

    private final int testNum = 42;

    private final Function1<Integer, Integer> func = new Function1<Integer, Integer>() {
        @Override
        public Integer apply(Integer number) {
            return number;
        }
    };

    private final Function1<Number, Integer> func2 = new Function1<Number, Integer>() {
        @Override
        public Integer apply(Number number) {
            return number.intValue();
        }
    };

    @Test
    public void testApply() throws Exception {
        final int testNum2 = 0;
        assertTrue(func.apply(testNum2) == testNum2);
        assertTrue((func2.apply(testNum)) == testNum);
    }

    @Test
    public void testCompose() throws Exception {
        assertTrue(func.compose(func2).apply(testNum) == testNum);
    }
}
