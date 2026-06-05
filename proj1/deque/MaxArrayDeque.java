package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private Comparator<T> com;
    public MaxArrayDeque(Comparator<T> c) {
        super();
        com = c;
    }

    public T max() {
        return max(com);
    }

    public T max(Comparator<T> c) {
        if (size() == 0) {
            return null;
        }
        T maxT = get(0);
        for (T i : this) {
            if (c.compare(i, maxT) > 0) {
                maxT = i;
            }
        }
        return maxT;
    }
}
