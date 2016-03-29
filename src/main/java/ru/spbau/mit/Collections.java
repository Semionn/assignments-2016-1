package ru.spbau.mit;

import java.util.ArrayList;

public final class Collections {

    private Collections() {}

    public static <T, R> Iterable<R> map(Function1<? super T, R> f, Iterable<T> collection) {
        ArrayList<R> result = new ArrayList<>();
        for (T t : collection) {
            result.add(f.apply(t));
        }
        return result;
    }

    public static <T> Iterable<T> filter(Predicate<? super T> predicate, Iterable<T> collection) {
        ArrayList<T> result = new ArrayList<>();
        for (T t : collection) {
            if (predicate.apply(t)) {
                result.add(t);
            }
        }
        return result;
    }

    public static <T> Iterable<T> takeWhile(Predicate<? super T> predicate, Iterable<T> collection) {
        ArrayList<T> result = new ArrayList<>();
        for (T t : collection) {
            if (!predicate.apply(t)) {
                break;
            }
            result.add(t);
        }
        return result;
    }

    public static <T> Iterable<T> takeUnless(Predicate<? super T> predicate, Iterable<T> collection) {
        return takeWhile(predicate.not(), collection);
    }

    public static <T, T2> T2 foldl(Function2<T2, ? super T, T2> func, T2 init, Iterable<T> collection) {
        T2 result = init;
        for (T t : collection) {
            result = func.apply(result, t);
        }
        return result;
    }

    public static <T, T2> T2 foldr(Function2<T2, ? super T, T2> func, T2 init, Iterable<T> collection) {
        ArrayList<T> temp = new ArrayList<>();
        for (T t : collection) {
            temp.add(t);
        }
        T2 result = init;
        for (int i = temp.size() - 1; i > 0; i--) {
            result = func.apply(result, temp.get(i));
        }
        return result;
    }

}
