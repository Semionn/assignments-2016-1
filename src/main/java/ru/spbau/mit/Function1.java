package ru.spbau.mit;

public abstract class Function1<T, R> implements IFunction1<T, R> {

    @Override
    public abstract R apply(T t);

    public <R2> IFunction1<T, R2> compose(final IFunction1<? super R, R2> g) {
        final Function1<T, R> f = this;
        return new Function1<T, R2>() {
            @Override
            public R2 apply(T t) {
                return g.apply(f.apply(t));
            }
        };
    }
}
