package ru.spbau.mit;

public abstract class Function2<T1, T2, R> implements IFunction1<T1, IFunction1<T2, R>> {

    public abstract R apply(T1 t1, T2 t2);

    @Override
    public IFunction1<T2, R> apply(T1 t1) {
        return bind1(t1);
    }

    public <R2> Function2<T1, T2, R2> compose(final IFunction1<? super R, R2> g) {
        final Function2<T1, T2, R> f = this;
        return new Function2<T1, T2, R2>() {
            @Override
            public R2 apply(T1 t1, T2 t2) {
                return g.apply(f.apply(t1, t2));
            }
        };
    }

    public Function1<T2, R> bind1(final T1 t1) {
        final Function2<T1, T2, R> f = this;
        return new Function1<T2, R>() {
            @Override
            public R apply(T2 t2) {
                return f.apply(t1, t2);
            }
        };
    }

    public Function1<T1, R> bind2(final T2 t2) {
        final Function2<T1, T2, R> f = this;
        return new Function1<T1, R>() {
            @Override
            public R apply(T1 t1) {
                return f.apply(t1, t2);
            }
        };
    }

    public Function1<T1, Function1<T2, R>> carry() {
        final Function2<T1, T2, R> f = this;
        return new Function1<T1, Function1<T2, R>>() {
            @Override
            public Function1<T2, R> apply(T1 t1) {
                return f.bind1(t1);
            }
        };
    }
}
