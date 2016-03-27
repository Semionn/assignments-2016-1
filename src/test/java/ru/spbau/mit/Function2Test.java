package ru.spbau.mit;

import org.junit.Test;

import static org.junit.Assert.*;

public class Function2Test {

    private final int firstArg = 3;
    private final int secondArg = 4;
    private final int answer = firstArg * firstArg + secondArg * secondArg;
    private final int multiplier = 2;

    private final int firstArg2 = 9;
    private final int secondArg2 = 2;
    private final int answer2 = firstArg2 - secondArg2;

    private Function2<Integer, Integer, Integer> func = new Function2<Integer, Integer, Integer>() {
        @Override
        public Integer apply(Integer a, Integer b) {
            return a * a + b * b;
        }
    };

    private Function2<Integer, Integer, Integer> func2 = new Function2<Integer, Integer, Integer>() {
        @Override
        public Integer apply(Integer number1, Integer number2) {
            return number1 - number2;
        }
    };

    @Test
    public void testApply() throws Exception {
        assertTrue(func.apply(firstArg, secondArg) == answer);
        assertTrue(func2.apply(firstArg2, secondArg2) == answer2);
    }

    @Test
    public void testApplyInherited() throws Exception {
        assertTrue(func2.apply(firstArg2).apply(secondArg2) == answer2);
    }

    @Test
    public void testCompose() throws Exception {
        int firstArg3 = 1;
        int secondArg3 = 2;
        int answer3 = (firstArg3 * firstArg3 + secondArg3 * secondArg3) * multiplier;

        Function1<Number, Integer> func3 = new Function1<Number, Integer>() {
            @Override
            public Integer apply(Number base) {
                return base.intValue();
            }
        };

        Function1<Integer, Integer> func4 = new Function1<Integer, Integer>() {
            @Override
            public Integer apply(Integer number) {
                return number * multiplier;
            }
        };

        assertTrue(func.compose(func4).apply(firstArg3, secondArg3) == answer3);
        assertTrue(func2.compose(func3).apply(firstArg2, secondArg2) == answer2);
    }

    @Test
    public void testBind1() throws Exception {
        assertTrue(func.bind1(firstArg).apply(secondArg) == answer);
        assertTrue(func2.bind1(firstArg2).apply(secondArg2) == answer2);
    }

    @Test
    public void testBind2() throws Exception {
        assertTrue(func.bind2(firstArg).apply(secondArg) == answer);
        assertTrue(func2.bind2(secondArg2).apply(firstArg2) == answer2);
    }

    @Test
    public void testCarry() throws Exception {
        assertTrue(func.carry().apply(firstArg).apply(secondArg) == answer);
        assertTrue(func2.carry().apply(firstArg).apply(secondArg) == firstArg - secondArg);
    }
}
