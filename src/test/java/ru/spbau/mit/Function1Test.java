package ru.spbau.mit;

import org.junit.Test;

import static org.junit.Assert.*;

public class Function1Test {

    private static final int TEST_NUM = 42;

    private static final Function1<Integer, Integer> IDENTITY_FUNC = new Function1<Integer, Integer>() {
        @Override
        public Integer apply(Integer number) {
            return number;
        }
    };

    private static final Function1<Number, Integer> GET_INTEGER_FUNC = new Function1<Number, Integer>() {
        @Override
        public Integer apply(Number number) {
            return number.intValue();
        }
    };

    @Test
    public void testApply() throws Exception {
        final int testNum2 = 0;
        assertTrue(IDENTITY_FUNC.apply(testNum2) == testNum2);
        assertTrue((GET_INTEGER_FUNC.apply(TEST_NUM)) == TEST_NUM);
    }

    @Test
    public void testCompose() throws Exception {
        assertTrue(IDENTITY_FUNC.compose(GET_INTEGER_FUNC).apply(TEST_NUM) == TEST_NUM);
    }
}
