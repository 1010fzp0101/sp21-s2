package deque;

import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;

public class ArrayDequeTest {
    @Test
    public void addFirstTest() {
        ArrayDeque<Integer> a = new ArrayDeque<>();
        for (int i = 0; i < 20; ++i) {
            a.addFirst(i);
        }
        a.printDeque();
        int s = a.size();
        /*
        for (int i = 0; i < s; ++i) {
            int m = a.removeFirst();
            a.printDeque();
        }
         */
        for (int i = 0; i < s; ++i) {
            int m = a.removeLast();
            a.printDeque();
        }
    }

    @Test
    public void addLastTest() {
        ArrayDeque<Integer> a = new ArrayDeque<>();
        for (int i = 0; i < 17; ++i) {
            a.addLast(i);
        }
        int s = a.size();
        for (int i = 0; i < s; ++i) {
            int m = a.removeFirst();
            a.printDeque();
        }
    }

    @Test
    public void addTest() {
        ArrayDeque<Integer> a = new ArrayDeque<>();
        for (int i = 0; i < 20; ++i) {
            a.addLast(i);
            a.addFirst(i + 1);
        }
        int s = a.size();
        for (int i = 0; i < s; ++i) {
            //int m = a.removeFirst();
            int m = a.removeLast();
            a.printDeque();
        }
        a.printDeque();
    }

    @Test
    public void getAndprintDequeTest() {
        ArrayDeque<Integer> a = new ArrayDeque<>();
        /* 3 4 5 6 7 0 1 2 */
        for (int i = 0; i < 8; ++i) {
            a.addLast(i);
            int m = a.get(i);
            assertEquals(m, i);
        }
        a.printDeque();
        int s = a.size();
        for (int i = 0; i < s; ++i) {
            int m = a.removeFirst();
            a.printDeque();
            assertEquals(m, i);
        }
    }

    @Test
    public void printDequeTest() {
        ArrayDeque<Integer> a = new ArrayDeque<>();
        a.addFirst(9);
        a.addLast(8);
        a.addFirst(1);
        a.addLast(7);
        a.printDeque();
        /* 1 9 8 7 */
        int m;
        m = a.removeFirst();
        a.printDeque();
        assertEquals(m, 1);
        m = a.removeFirst();
        a.printDeque();
        assertEquals(m, 9);
    }

    @Test
    public void equalsTest() {
        ArrayDeque<Integer> a = new ArrayDeque<>();
        ArrayDeque<Integer> b = new ArrayDeque<>();
        a.addLast(1);
        b.addFirst(2);
        boolean bo = a.equals(b);
        assertEquals(bo, false);
        a.addFirst(2);
        b.addLast(1);
        bo = a.equals(b);
        assertEquals(bo, true);
    }



}
