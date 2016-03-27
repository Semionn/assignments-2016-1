package ru.spbau.mit;

public abstract class Predicate<T> extends Function1<T, Boolean> {

    public static final Predicate ALWAYS_TRUE = new Predicate() {
        @Override
        public Boolean apply(Object o) {
            return true;
        }
    };

    public static final Predicate ALWAYS_FALSE = new Predicate() {
        @Override
        public Boolean apply(Object o) {
            return false;
        }
    };

    public abstract Boolean apply(T t);

    public Predicate<T> or(final Predicate<? super T> p) {
        return new Predicate<T>() {
            @Override
            public Boolean apply(T t) {
                return Predicate.this.apply(t) || p.apply(t);
            }
        };
    }

    public Predicate<T> and(final Predicate<? super T> p) {
        return new Predicate<T>() {
            @Override
            public Boolean apply(T t) {
                return Predicate.this.apply(t) && p.apply(t);
            }
        };
    }

    public Predicate<T> not() {
        return new Predicate<T>() {
            @Override
            public Boolean apply(T t) {
                return !Predicate.this.apply(t);
            }
        };
    }
}
