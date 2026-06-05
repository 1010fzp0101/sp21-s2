package deque;


public interface Deque<T> extends Iterable<T> {
    void addFirst(T item);
    void addLast(T item);
    default boolean isEmpty() {
        if (size() > 0) {
            return false;
        }
        return true;
    }
    int size();
    void printDeque();
    T removeFirst();
    T removeLast();
    T get(int index);
}
