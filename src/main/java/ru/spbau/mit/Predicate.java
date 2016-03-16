package ru.spbau.mit;

public abstract class Predicate<T> {

    public abstract boolean apply(T t);

    public Predicate<T> or(final Predicate<T> p2) {
        final Predicate<T> p1 = this;
        return new Predicate<T>() {
            @Override
            public boolean apply(T t) {
                return p1.apply(t) || p2.apply(t);
            }
        };
    }

    public Predicate<T> and(final Predicate<T> p2) {
        final Predicate<T> p1 = this;
        return new Predicate<T>() {
            @Override
            public boolean apply(T t) {
                return p1.apply(t) && p2.apply(t);
            }
        };
    }

    public Predicate<T> not() {
        final Predicate<T> p1 = this;
        return new Predicate<T>() {
            @Override
            public boolean apply(T t) {
                return !p1.apply(t);
            }
        };
    }
}
