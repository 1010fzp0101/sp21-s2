package hashmap;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.LinkedList;
import java.util.HashSet;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    // You should probably define some more!
    private int initialSize = 16;
    private double loadFactor = 0.75;
    private HashSet<K> set = new HashSet<>();

    /** Constructors */
    public MyHashMap() {
        this(16);
    }

    public MyHashMap(int initialSize) {
        this(initialSize, 0.75);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        this.initialSize = initialSize;
        this.loadFactor = maxLoad;
        buckets = createTable(initialSize);
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<Node>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        Collection<Node>[] buckets = new Collection[tableSize];
        for (int i = 0; i < initialSize; ++i) {
            buckets[i] = createBucket();
        }
        return buckets;
    }

    // TODO: Implement the methods of the Map61B Interface below
    // Your code won't compile until you do so!


    @Override
    public Iterator<K> iterator() {
        return set.iterator();
    }



    @Override
    public void clear() {
        for (K i : set) {
            remove(i);
        }
        set.clear();
    }

    @Override
    public boolean containsKey(K key) {
        return get(key) != null;
    }

    @Override
    public V get(K key) {
        int index = getIndex(key);
        for (Node i : buckets[index]) {
            if (i.key.equals(key)) {
                return i.value;
            }
        }
        return null;
    }

    @Override
    public int size() {
        return set.size();
    }

    @Override
    public void put(K key, V value) {
        int index = getIndex(key);
        if (containsKey(key)) {
            remove(key, get(key));
        }
        Node node = new Node(key, value);
        buckets[index].add(node);
        set.add(key);
        if ((float) size() / initialSize >= loadFactor) {
            resize(2 * initialSize);
            initialSize *= 2;
        }
    }

    private void resize(int chains) {
        MyHashMap<K, V> newHashMap = new MyHashMap(chains, loadFactor);
        for (K i : set) {
            V val = get(i);
            newHashMap.put(i, val);
        }
        buckets = newHashMap.buckets;
    }

    @Override
    public Set<K> keySet() {
        return set;
    }

    @Override
    public V remove(K key) {
        int index = getIndex(key);
        for (Node i : buckets[index]) {
            if (i.key.equals(key)) {
               V val = i.value;
               buckets[index].remove(i);
               set.remove(i);
               return val;
            }
        }
        return null;
    }

    @Override
    public V remove(K key, V value) {
        int index = getIndex(key);
        for (Node i : buckets[index]) {
            if (i.key.equals(key) && i.value == value) {
                buckets[index].remove(i);
                set.remove(i.key);
                return value;
            }
        }
        return null;
    }

    private int getIndex(K key) {
        if (key.hashCode() < 0) {
            return -1 * key.hashCode() % initialSize;
        }
        return key.hashCode() % initialSize;
    }

}
