package ru.spbau.mit;

import org.junit.Test;

import static org.junit.Assert.*;

public class Function2Test {

    private static final int FIRST_ARG = 3;
    private static final int SECOND_ARG = 4;
    private static final int ANSWER = FIRST_ARG * FIRST_ARG + SECOND_ARG * SECOND_ARG;
    private static final int MULTIPLIER = 2;

    private static final int FIRST_ARG_2 = 9;
    private static final int SECOND_ARG_2 = 2;
    private static final int ANSWER_2 = FIRST_ARG_2 - SECOND_ARG_2;

    private static final Function2<Integer, Integer, Integer> SQUARES_SUM_FUNC
            = new Function2<Integer, Integer, Integer>() {
        @Override
        public Integer apply(Integer a, Integer b) {
            return a * a + b * b;
        }
    };

    private static final Function2<Integer, Integer, Integer> DIFF_FUNC
            = new Function2<Integer, Integer, Integer>() {
        @Override
        public Integer apply(Integer number1, Integer number2) {
            return number1 - number2;
        }
    };

    @Test
    public void testApply() throws Exception {
        assertTrue(SQUARES_SUM_FUNC.apply(FIRST_ARG, SECOND_ARG) == ANSWER);
        assertTrue(DIFF_FUNC.apply(FIRST_ARG_2, SECOND_ARG_2) == ANSWER_2);
    }

    @Test
    public void testApplyInherited() throws Exception {
        assertTrue(DIFF_FUNC.apply(FIRST_ARG_2).apply(SECOND_ARG_2) == ANSWER_2);
    }

    @Test
    public void testCompose() throws Exception {
        int firstArg3 = 1;
        int secondArg3 = 2;
        int answer3 = (firstArg3 * firstArg3 + secondArg3 * secondArg3) * MULTIPLIER;

        Function1<Number, Integer> func3 = new Function1<Number, Integer>() {
            @Override
            public Integer apply(Number base) {
                return base.intValue();
            }
        };

        Function1<Integer, Integer> func4 = new Function1<Integer, Integer>() {
            @Override
            public Integer apply(Integer number) {
                return number * MULTIPLIER;
            }
        };

        assertTrue(SQUARES_SUM_FUNC.compose(func4).apply(firstArg3, secondArg3) == answer3);
        assertTrue(DIFF_FUNC.compose(func3).apply(FIRST_ARG_2, SECOND_ARG_2) == ANSWER_2);
    }

    @Test
    public void testBind1() throws Exception {
        assertTrue(SQUARES_SUM_FUNC.bind1(FIRST_ARG).apply(SECOND_ARG) == ANSWER);
        assertTrue(DIFF_FUNC.bind1(FIRST_ARG_2).apply(SECOND_ARG_2) == ANSWER_2);
    }

    @Test
    public void testBind2() throws Exception {
        assertTrue(SQUARES_SUM_FUNC.bind2(FIRST_ARG).apply(SECOND_ARG) == ANSWER);
        assertTrue(DIFF_FUNC.bind2(SECOND_ARG_2).apply(FIRST_ARG_2) == ANSWER_2);
    }

    @Test
    public void testCarry() throws Exception {
        assertTrue(SQUARES_SUM_FUNC.carry().apply(FIRST_ARG).apply(SECOND_ARG) == ANSWER);
        assertTrue(DIFF_FUNC.carry().apply(FIRST_ARG).apply(SECOND_ARG) == FIRST_ARG - SECOND_ARG);
    }
}
