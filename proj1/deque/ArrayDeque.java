package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T>, Iterable<T> {
    private int size;
    private int nextFirst;
    private int nextLast;
    private T[] arr;

    public ArrayDeque() {
        size = 0;
        arr = (T[]) new Object[8];
        nextFirst = 4;
        nextLast = 5;
    }

    private void resize(int capacity) {
       T[] a = (T[]) new Object[capacity];
       for (int i = 0; i < size(); ++i) {
           a[i] = get(i);
       }
       arr = a;
       nextFirst = capacity - 1;
       nextLast = size();
    }

    @Override
    public void addFirst(T item) {
        if (size == arr.length) {
            resize(size * 2);
        }
        arr[nextFirst] = item;
        nextFirst = decreaseIndex(nextFirst);
        size += 1;
    }

    @Override
    public void addLast(T item) {
        if (size == arr.length) {
            resize(size * 2);
        }
        arr[nextLast] = item;
        nextLast = increaseIndex(nextLast);
        size += 1;
    }

    @Override
    public int size() {
        return size;
    }



    @Override
    public T get(int index) {
        return arr[(increaseIndex(nextFirst) + index) % arr.length];
    }

    @Override
    public void printDeque() {
        for (int i = 0; i < size; ++i) {
            System.out.print(get(i) + " ");
        }
        System.out.println();
    }

    @Override
    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        int front = increaseIndex(nextFirst);
        T elmt = arr[front];
        arr[front] = null;
        nextFirst = front;
        size -= 1;
        if (arr.length >= 16 && (float) size / arr.length < 0.25) {
            resize(arr.length / 2);
        }
        return elmt;
    }

    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        }
        nextLast = decreaseIndex(nextLast);
        T elmt = arr[nextLast];
        arr[nextLast] = null;
        size -= 1;
        if (arr.length >= 16 && (float) size / arr.length < 0.25) {
            resize(arr.length / 2);
        }
        return elmt;
    }

    public boolean equals(Object o) {
        if (!(o instanceof Deque<?>)) {
            return false;
        }
        if (((Deque<?>) o).size() != size()) {
            return false;
        }
        for (int i = 0; i < size; ++i) {
            if (!get(i).equals(((Deque<?>) o).get(i))) {
                return false;
            }
        }
        return true;
    }

    private int decreaseIndex(int i) {
        if (i == 0) {
            return arr.length - 1;
        }
        return i - 1;
    }

    private int increaseIndex(int i) {
        if (i == arr.length - 1) {
            return 0;
        }
        return i + 1;
    }

    public Iterator<T> iterator() {
        return new ArraySetIterator();
    }

    private class ArraySetIterator implements Iterator<T> {
        private int pos;
        ArraySetIterator() {
            pos = 0;
        }

        @Override
        public boolean hasNext() {
            return pos < size();
        }

        @Override
        public T next() {
            T returnT = get(pos);
            pos += 1;
            return returnT;
        }
    }
}
