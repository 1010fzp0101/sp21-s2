package deque;

import java.util.ArrayDeque;
import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T> {
    private class deque<T> {
        private T elem;
        private deque<T> prev;
        private deque<T> next;
        deque(T theitem, deque<T> p, deque<T> n) {
            elem = theitem;
            prev = p;
            next = n;
        }

    }
    private int size;
    private deque<T> sentinel;

    /*Create an empty linked list deque*/
    public LinkedListDeque() {
        size = 0;
        sentinel = new deque<T>(null, null, null);
    }

    /*Return the number of items in the deque.*/
    @Override
    public int size() {
        return size;
    }


    /*Add an item of type T to the front of the deque.*/
    @Override
    public void addFirst(T item) {
        deque<T> m = new deque<T>(item, null, null);
        if (size() == 0) {
            m.next = m;
            m.prev = m;
            sentinel.next = m;
        } else {
            m.next = sentinel.next;
            m.prev = sentinel.next.prev;
            sentinel.next.prev.next = m;
            sentinel.next.prev = m;
            sentinel.next = m;
        }
        size += 1;
    }


    /*print the deque*/
    @Override
    public void printDeque() {
        deque<T> tmp;
        tmp = sentinel.next;
        int n = 0;
        while (tmp != null && n < size) {
            System.out.print(tmp.elem + " ");
            tmp = tmp.next;
            n += 1;
        }
        System.out.println();
    }

    /*Add an item of type T to the back of the deque.*/
    @Override
    public void addLast(T item) {
        deque<T> m = new deque<T>(item, null, null);
        if (size() == 0) {
            m.next = m;
            m.prev = m;
            sentinel.next = m;
        } else {
            m.next = sentinel.next;
            m.prev = sentinel.next.prev;
            sentinel.next.prev.next = m;
            sentinel.next.prev = m;
        }
        size += 1;
    }

    /*remove and return the first item of the deque*/
    @Override
    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        size -= 1;
        deque<T> d = sentinel.next;
        T tmp = sentinel.next.elem;
        sentinel.next.prev.next = sentinel.next.next;
        sentinel.next.next.prev = sentinel.next.prev;
        sentinel.next = sentinel.next.next;
        d = null;
        return tmp;
    }

    /*remove and return the last value of the deque.*/
    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        }
        size -= 1;
        deque<T> d = sentinel.next.prev;
        T m = d.elem;
        sentinel.next.prev = d.prev;
        d.prev.next = sentinel.next;
        d = null;
        return m;
    }

    /*get the item at the given index, where 0 is the front, and 1 is the next,
    *and so forth.
    */
    @Override
    public T get(int index)  {
        if (index < 0 || index >= size) {
            return null;
        } else if (index < size / 2) {
            deque<T> d = sentinel;
            for (int i = 0; i <= index; ++i) {
                d = d.next;
            }
            return d.elem;
        } else {
            deque<T> d = sentinel.next;
            for (int i = 0; i < size - index; ++i) {
                d = d.prev;
            }
            return d.elem;
        }
    }

    /*similer as get, but use recursion*/
    public T getRecursive(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        if (index < size / 2) {
            return getRecursiveUsingNext(index, sentinel.next);
        } else {
            return getRecursiveUsingPrev(size - index, sentinel.next);
        }
    }

    /*the helper function of getRecursive.*/
    private T getRecursiveUsingNext(int index, deque<T> d) {
        if (index == 0) {
            return d.elem;
        }
        return getRecursiveUsingNext(index - 1, d.next);
    }

    private T getRecursiveUsingPrev(int index, deque<T> d) {
        if (index == 0) {
            return d.elem;
        }
        return getRecursiveUsingPrev(index - 1, d.prev);
    }


    /*Returns whether the object o is the same object of deque. o
    *is considered equal if it is a deque and if it contains the same contents
    * in the same order.
     */

    public boolean equals(Object o) {
        if (!(o instanceof Deque)) {
            return false;
        }

        if (((Deque<?>) o).size() != size()) {
            return false;
        }
        for (int i = 0; i < size; ++i) {
            if (!get(i).equals(((Deque<?>) o).get(i)))  {
                return false;
            }
        }
        return true;
    }


    public Iterator<T> iterator() {
        return new LinkedListSetIterator();
    }

    private class LinkedListSetIterator implements Iterator<T> {
        private int _pos;
        public LinkedListSetIterator() {
            _pos = 0;
        }


        @Override
        public boolean hasNext() {
            return _pos < size();
        }

        @Override
        public T next() {
            T returnT = get(_pos);
            _pos += 1;
            return returnT;
        }
    }
}
