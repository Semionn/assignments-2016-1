package ru.spbau.mit;

import org.junit.Test;

import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.Iterator;

public class FunctionalTest {

    @Test
    public void function1Test() {
        Function1<Integer, Derived> func = new Function1<Integer, Derived>() {
            @Override
            public Derived apply(Integer number) {
                return new Derived(number);
            }
        };

        final int testNum = 0;
        assertTrue(func.apply(testNum).getState() == testNum);

        Function1<Base, Integer> func2 = new Function1<Base, Integer>() {
            @Override
            public Integer apply(Base obj) {
                return obj.state;
            }
        };

        final int testNum2 = 42;
        assertTrue((func2.apply(new Base(testNum2))) == testNum2);
        assertTrue(func.compose(func2).apply(testNum2) == testNum2);
    }

    @Test
    public void function2Test() {
        Function2<Integer, Integer, Integer> func = new Function2<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer a, Integer b) {
                return a * a + b * b;
            }
        };

        final int firstArg = 3;
        final int secondArg = 4;
        final int answer = firstArg * firstArg + secondArg * secondArg;

        assertTrue(func.apply(firstArg, secondArg) == answer);
        assertTrue(func.bind1(firstArg).apply(secondArg) == answer);
        assertTrue(func.bind2(firstArg).apply(secondArg) == answer);
        assertTrue(func.carry().apply(firstArg).apply(secondArg) == answer);

        final int multiplier = 2;
        Function1<Integer, Integer> func1 = new Function1<Integer, Integer>() {
            @Override
            public Integer apply(Integer number) {
                return number * multiplier;
            }
        };

        final int firstArg2 = 1;
        final int secondArg2 = 2;
        final int answer2 = (firstArg2 * firstArg2 + secondArg2 * secondArg2) * multiplier;

        assertTrue(func.compose(func1).apply(firstArg2, secondArg2) == answer2);

        Function2<Integer, Integer, Derived> func2 = new Function2<Integer, Integer, Derived>() {
            @Override
            public Derived apply(Integer number1, Integer number2) {
                return new Derived(number1 - number2);
            }
        };

        final int firstArg3 = 9;
        final int secondArg3 = 2;
        final int answer3 = firstArg3 - secondArg3;

        assertTrue(func2.apply(firstArg3, secondArg3).getState() == answer3);
        assertTrue(func2.apply(firstArg3).apply(secondArg3).getState() == answer3);
        assertTrue(func2.bind1(firstArg3).apply(secondArg3).getState() == answer3);
        assertTrue(func2.bind2(secondArg3).apply(firstArg3).getState() == answer3);

        Function1<Base, Integer> func3 = new Function1<Base, Integer>() {
            @Override
            public Integer apply(Base base) {
                return base.state;
            }
        };

        assertTrue(func2.compose(func3).apply(firstArg3, secondArg3) == answer3);
    }

    @Test
    public void predicateTest() {

        final int checkNum = 42;
        Predicate<Derived> predicate = new Predicate<Derived>() {
            @Override
            public Boolean apply(Derived d) {
                return d.getState() == checkNum;
            }
        };

        assertTrue(predicate.apply(new Derived(checkNum)));
        assertFalse(predicate.not().apply(new Derived(checkNum)));

        final int checkNum2 = 0;
        Predicate<Derived> predicate2 = new Predicate<Derived>() {
            @Override
            public Boolean apply(Derived d) {
                return d.getState() > checkNum2;
            }
        };

        assertTrue(predicate.and(predicate2).apply(new Derived(checkNum)));
        assertFalse(predicate.and(predicate2).apply(new Derived(-checkNum)));
        assertTrue(predicate.or(predicate2).apply(new Derived(1 + checkNum)));
        assertFalse(predicate.or(predicate2).apply(new Derived(checkNum2 - 1)));

        Function1<Boolean, Integer> func = new Function1<Boolean, Integer>() {
            @Override
            public Integer apply(Boolean b) {
                if (b) {
                    return 1;
                }
                return 0;
            }
        };

        assertTrue(predicate.compose(func).apply(new Derived(checkNum)) == 1);
        assertTrue(predicate.compose(func).apply(new Derived(-checkNum)) == 0);
    }

    @Test
    public void collectionsTest() {
        ArrayList<Derived> collection = new ArrayList<>();

        final int initSize = 10;

        for (int i = 0; i < initSize; i++) {
            collection.add(new Derived(i));
        }

        final int threshold = 5;
        Predicate<Base> predicate = new Predicate<Base>() {
            @Override
            public Boolean apply(Base b) {
                return b.getState() < threshold;
            }
        };

        int counter = 0;
        Iterator<Derived> it1 = Collections.filter(predicate, collection).iterator();
        Iterator<Derived> it2 = Collections.takeWhile(predicate, collection).iterator();
        Iterator<Derived> it3 = Collections.takeUnless(predicate.not(), collection).iterator();
        while (it1.hasNext() && it2.hasNext() && it3.hasNext()) {
            Derived d1 = it1.next();
            Derived d2 = it2.next();
            Derived d3 = it3.next();
            assertTrue(d1.getState() == counter && predicate.apply(d1));
            assertTrue(d2.getState() == counter && predicate.apply(d2));
            assertTrue(d3.getState() == counter && predicate.apply(d3));
            counter++;
        }
        assertTrue(it1.hasNext() == it2.hasNext() && it1.hasNext() == it3.hasNext());
        assertTrue(counter == threshold);
        assertTrue(collection.size() == initSize);

        final int divisor = 2;
        Function1<Base, Double> func = new Function1<Base, Double>() {
            @Override
            public Double apply(Base base) {
                return (double) base.getState() / divisor;
            }
        };

        Iterator<Derived> it = collection.iterator();
        Iterator<Double> itTemp = Collections.map(func, collection).iterator();
        while (it.hasNext() && itTemp.hasNext()) {
            assertTrue((double) it.next().getState() / divisor == itTemp.next());
        }
        assertTrue(it.hasNext() == itTemp.hasNext());

        Function2<Integer, Base, Integer> func2 = new Function2<Integer, Base, Integer>() {
            @Override
            public Integer apply(Integer number, Base base) {
                return base.getState() + number;
            }
        };

        int sum = 0;
        for (Derived d : collection) {
            sum += d.getState();
        }

        assertTrue(Collections.foldl(func2, 0, collection) == sum);
        assertTrue(Collections.foldr(func2, 0, collection) == sum);
    }

    private class Base {
        private int state;

        Base(int state) {
            this.state = state;
        }

        public int getState() {
            return state;
        }
    }

    private class Derived extends Base {
        Derived(int state) {
            super(state);
        }
    }

}
