package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T>, Iterable<T> {
    private int size;
    private int nextFirst;
    private int nextLast;
    private T[] arr;
    private int front;

    public ArrayDeque() {
        size = 0;
        arr = (T[]) new Object[8];
        nextFirst = 4;
        nextLast = 5;
        front = nextLast;
    }

    private void resize(int capacity) {
        if (capacity > arr.length) {
            T[] a = (T[]) new Object[capacity];
            System.arraycopy(arr, 0, a, 0, nextLast);
            System.arraycopy(arr, nextLast, a, capacity - size + nextLast, size - nextLast);
            arr = a;
            nextFirst = arr.length - (size - nextFirst);
            front = increaseIndex(nextFirst);
        } else if (capacity < arr.length) {
            if (nextFirst >= 1 + nextLast) {
                T[] a = (T[]) new Object[capacity];
                System.arraycopy(arr, 0, a, 0, nextLast + 1);
                System.arraycopy(arr, nextFirst + 1,
                                 a, a.length - (arr.length - nextFirst) + 1,
                           arr.length - nextFirst - 1);
                int m = arr.length;
                arr = a;
                nextFirst = arr.length - (m - nextFirst);
                front = increaseIndex(nextFirst);
            } else {
                T[] a = (T[]) new Object[capacity];
                System.arraycopy(arr, nextFirst + 1, a, 0, nextLast - nextFirst - 1);
                arr = a;
                nextLast = nextLast - nextFirst - 1;
                nextFirst = capacity - 1;
                front = increaseIndex(nextFirst);
            }
        }
    }

    @Override
    public void addFirst(T item) {
        if (size == arr.length) {
            resize(size * 2);
        }
        arr[nextFirst] = item;
        front = nextFirst;
        nextFirst = decreaseIndex(nextFirst);
        size += 1;
    }

    @Override
    public void addLast(T item) {
        if (size == arr.length) {
            resize(size * 2);
        }
        if (arr[nextFirst] != null) {
            front = nextFirst;
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
        return arr[(front + index) % arr.length];
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
        if (arr.length >= 16) {
            if ((float) (size - 1) / arr.length < 0.25) {
                resize(arr.length / 2);
            }
            front = increaseIndex(nextFirst);
            T elmt = arr[front];
            arr[front] = null;
            nextFirst = front;
            front = increaseIndex(nextFirst);
            size -= 1;
            return elmt;
        } else {
            if (size == 0) {
                return null;
            } else {
                T elmt = arr[front];
                arr[front] = null;
                nextFirst = front;
                front = increaseIndex(nextFirst);
                size -= 1;
                return elmt;
            }
        }
    }

    @Override
    public T removeLast() {
        if (arr.length >= 16) {
            if ((float) (size - 1) / arr.length < 0.25) {
                resize(arr.length / 2);
            }
            nextLast = decreaseIndex(nextLast);
            T elmt = arr[nextLast];
            arr[nextLast] = null;
            size -= 1;
            return elmt;
        } else {
            if (size == 0) {
                return null;
            } else {
                nextLast = decreaseIndex(nextLast);
                T elmt = arr[nextLast];
                arr[nextLast] = null;
                size -= 1;
                return elmt;
            }
        }
    }

    public boolean equals(Object o) {
        if (!(o instanceof Deque<?>)) {
            return false;
        }
        if (((Deque<?>) o).size() != size()) {
            return false;
        }
        for (int i = 0; i < size; ++i) {
            if (!get(i).equals(((Deque<?>) o).get(i))){
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
