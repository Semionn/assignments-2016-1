package ru.spbau.mit;

public abstract class Predicate<T> extends Function1<T, Boolean> {

    public abstract Boolean apply(T t);

    public Predicate<T> or(final Predicate<? super T> p2) {
        final Predicate<T> p1 = this;
        return new Predicate<T>() {
            @Override
            public Boolean apply(T t) {
                return p1.apply(t) || p2.apply(t);
            }
        };
    }

    public Predicate<T> and(final Predicate<? super T> p2) {
        final Predicate<T> p1 = this;
        return new Predicate<T>() {
            @Override
            public Boolean apply(T t) {
                return p1.apply(t) && p2.apply(t);
            }
        };
    }

    public Predicate<T> not() {
        final Predicate<T> p1 = this;
        return new Predicate<T>() {
            @Override
            public Boolean apply(T t) {
                return !p1.apply(t);
            }
        };
    }
}
