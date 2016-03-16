package ru.spbau.mit;

public abstract class Function1<R, T> {

    public abstract R apply(T t);

    public <R2> Function1<R2, T> compose(final Function1<R2, ? super R> g) {
        final Function1<R, T> f = this;
        return new Function1<R2, T>() {
            @Override
            public R2 apply(T t) {
                return g.apply(f.apply(t));
            }
        };
    }
}
