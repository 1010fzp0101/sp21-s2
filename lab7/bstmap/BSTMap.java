package bstmap;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

import java.util.Iterator;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {
    private BST<K, V> StoreTree;

    BSTMap() {
        StoreTree = new BST<>();
    }

    @Override
    public V remove(K key, V value) {
        if (StoreTree.get(key) == value) {
            V val = remove(key);
            return val;
        }
        return null;
    }

    @Override
    public V remove(K key) {
        V val = get(key);
        StoreTree.delete(key);
        return val;
    }

    @Override
    public Set<K> keySet() {
        Set<K> s = new HashSet<>();
        for (int i =0; i < size(); i++) {
            s.add(StoreTree.select(i));
        }
        return s;
    }

    @Override
    public Iterator<K> iterator() {
        return new SetIterator();
    }

    public class SetIterator implements Iterator<K> {
        int index = 0;
        @Override
        public boolean hasNext() {
            return index < size();
        }

        @Override
        public K next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return StoreTree.select(index);
        }
    }

    @Override
    public void clear() {
        while (!StoreTree.isEmpty()) {
            StoreTree.deleteMin();
        }
    }

    @Override
    public boolean containsKey(K key) {
        if (StoreTree.rank(key) != -1) {
            return true;
        }
        return false;
    }

    @Override
    public V get(K key) {
        return StoreTree.get(key);
    }

    @Override
    public void put(K key, V value) {
        StoreTree.put(key, value);
    }

    @Override
    public int size() {
        return StoreTree.size();
    }
}
