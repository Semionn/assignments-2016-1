package ru.spbau.mit;

import java.util.*;

/**
 * Created by semionn on 22.03.16.
 */
public class HashMultiset<T> extends AbstractCollection<T> implements Multiset<T> {

    private LinkedHashMap<T, MultiElem> map = new LinkedHashMap<>();
    private AbstractSet<MultiElem> set = new LinkedHashSet<>();
    private int size = 0;

    private class MultiElem implements Entry<T> {
        private T elem;
        private int count;

        MultiElem(T elem) {
            this.elem = elem;
            count = 1;
        }

        @Override
        public T getElement() {
            return elem;
        }

        @Override
        public int getCount() {
            return count;
        }
    }

    private class HashMultisetIterator implements Iterator {

        private MultiElem current, next;
        private Iterator<MultiElem> setIterator;
        private Iterator<T> maptIterator;
        private int currentElemIndex;

        HashMultisetIterator() {
            setIterator = set.iterator();
            maptIterator = map.keySet().iterator();
            currentElemIndex = 0;
            if (setIterator.hasNext()) {
                next = setIterator.next();
                maptIterator.next();
            }
        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public Object next() {
            if (current != null && currentElemIndex < current.count - 1) {
                currentElemIndex++;
                return current.elem;
            }

            current = next;
            currentElemIndex = 0;

            if (setIterator.hasNext()) {
                next = setIterator.next();
                maptIterator.next();
            } else {
                next = null;
            }
            return current.elem;
        }

        @Override
        public void remove() {
            if (current.count > 1) {
                current.count--;
                currentElemIndex--;
            } else {
                maptIterator.remove();
                setIterator.remove();
            }
            size--;
        }
    }

    @Override
    public int count(Object element) {
        if (!map.containsKey(element)) {
            return 0;
        }
        return map.get(element).count;
    }

    @Override
    public Set elementSet() {
        return map.keySet();
    }

    @Override
    public Set<? extends Entry<T>> entrySet() {
        return set;
    }

    @Override
    public Iterator iterator() {
        return new HashMultisetIterator();
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean add(T t) {
        MultiElem elem = new MultiElem(t);
        size++;
        if (map.containsKey(t)) {
            map.get(t).count++;
        } else {
            map.put(t, elem);
            set.add(elem);
        }
        return true;
    }
}
