package ru.spbau.mit;

public abstract class Function2<R, T1, T2> {

    public abstract R apply(T1 t1, T2 t2);

    public <R2>  Function2<R2, T1, T2> compose(final Function1<R2, ? super R> g) {
        final Function2<R, T1, T2> f = this;
        return new Function2<R2, T1, T2>() {
            @Override
            public R2 apply(T1 t1, T2 t2) {
                return g.apply(f.apply(t1, t2));
            }
        };
    }

    public Function1<R, T2> bind1(final T1 t1) {
        final Function2<R, T1, T2> f = this;
        return new Function1<R, T2>() {
            @Override
            public R apply(T2 t2) {
                return f.apply(t1, t2);
            }
        };
    }

    public Function1<R, T1> bind2(final T2 t2) {
        final Function2<R, T1, T2> f = this;
        return new Function1<R, T1>() {
            @Override
            public R apply(T1 t1) {
                return f.apply(t1, t2);
            }
        };
    }

    public Function1<Function1<R, T2>, T1> carry() {
        final Function2<R, T1, T2> f = this;
        return new Function1<Function1<R, T2>, T1>() {
            @Override
            public Function1<R, T2> apply(T1 t1) {
                return f.bind1(t1);
            }
        };
    }
}
