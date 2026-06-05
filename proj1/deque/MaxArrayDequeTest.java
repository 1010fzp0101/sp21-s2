package deque;

import org.junit.Test;

import java.util.Comparator;
import java.util.Optional;

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
        a.addFirst(1);
        a.addLast(2);
        int m = a.max();
        assertEquals(m, 2);
    }
}
