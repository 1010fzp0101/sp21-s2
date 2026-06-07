package deque;

import org.junit.Test;

import java.util.Comparator;

import static org.junit.Assert.*;

public class MaxArrayDequeTest {
    public class mycompara<T> implements Comparator<T> {
        @Override
        public int compare(T o1, T o2) {
            if (o1 instanceof Integer) {
                return Double.compare(((Integer) o1).doubleValue(), ((Integer)o2).doubleValue());
            }
            return 0;
        }
    }


    @Test
    public void test1() {
        Comparator<Integer> c = new mycompara<>();
        MaxArrayDeque<Integer> a = new MaxArrayDeque(c);

        a.addLast(2);
        int m = a.max();
        assertEquals(m, 2);
    }

    @Test
    public void test2() {
        MaxArrayDeque<Integer> a = new MaxArrayDeque<>(new mycompara<>());
        a.addFirst(0);
        a.get(0);
        a.removeLast();
        a.addFirst(3);
        a.addFirst(4);
        a.removeFirst();
        a.addLast(6);
        a.get(1);
        a.addLast(8);
        a.addLast(9);
        a.addLast(10);
        a.addFirst(11);
        a.addFirst(12);
        a.addLast(13);
        a.removeLast();
        a.addFirst(15);
        a.removeLast();
        a.addLast(17);
        a.addLast(18);
        int x = a.get(5);
        assertEquals(x, 8);
    }
}
