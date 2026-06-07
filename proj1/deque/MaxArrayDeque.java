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
        if (isEmpty()) {
            return null;
        }
        T maxT = get(0);
        for (int i = 1; i < size(); ++i) {
            if (c.compare(get(i), maxT) > 0) {
                maxT = get(i);
            }
        }
        return maxT;
    }
}
