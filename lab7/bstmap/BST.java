package bstmap;

import java.util.NoSuchElementException;

public class BST<Key extends Comparable<Key>, Value> {
    private Node root;
    private class Node {
        private Key key;
        private Value val;
        private Node left, right;
        private int size;

        Node (Key key, Value val, int size) {
            this.key = key;
            this.val = val;
            this.size = size;
        }
    }

    public BST() {
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public int size() {
        return size(root);
    }

    private int size(Node node) {
        if (node == null) {
            return 0;
        }
        return node.size;
    }

    public boolean contains(Key key) {
        if (key == null) {
            throw new IllegalArgumentException("argument to contains() is null");
        }
        return get(key) != null;
    }

    public Value get(Key key) {
        return get(root, key);
    }

    private Value get(Node node, Key key) {
        if (key == null) {
            throw new IllegalArgumentException("argument to contains() is null");
        }
        if (node == null) {
            return null;
        }
        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            return get(node.left, key);
        } else if (cmp > 0) {
            return get(node.right, key);
        } else {
            return node.val;
        }
    }

    public void put(Key key, Value val) {
        if (key == null) {
            throw new IllegalArgumentException("argument to contains() is null");
        }

        root = put(root, key, val);
    }

    public void deleteMin() {
        if (isEmpty()) {
            throw new NoSuchElementException("Symbol table underflow");
        }
        root = deleteMin(root);
    }

    private Node deleteMin(Node node) {
        if (node.left == null) {
            return node.right;
        }
        node.left = deleteMin(node.left);
        node.size = size(node.left) + size(node.right) + 1;
        return node;
    }


    public void deleteMax() {
        if (isEmpty()) {
            throw new NoSuchElementException("Symbol table underflow");
        }
        root = deleteMax(root);
    }

    private Node deleteMax(Node node) {
        if (node.left == null) {
            return node.left;
        }
        node.right = deleteMin(node.right);
        node.size = size(node.left) + size(node.right) + 1;
        return node;
    }

    public void delete(Key key) {
        if (key == null) {
            throw new IllegalArgumentException("calls new delete() with a null key");
        }
        root = delete(root, key);
    }

    private Node delete(Node node, Key key) {
        if (node == null) {
            return null;
        }
        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = delete(node.left, key);
        } else if (cmp > 0) {
            node.right = delete(node.right, key);
        } else {
            if (node.right == null) {
                return node.left;
            }
            if (node.left == null) {
                return node.right;
            }
            Node temp = node;
            node = min(node.right);
            node.right = deleteMin(temp.right);
            node.left = temp.left;
        }
        node.size = size(node.left) + size(node.right) + 1;
        return node;
    }

    private Node put(Node node, Key key, Value val) {
        if (node == null) {
            return new Node(key, val, 1);
        }
        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = put(node.left, key, val);
        } else if (cmp > 0) {
            node .right = put(node.right, key, val);
        } else {
            node.val = val;
        }
        node.size = 1 + size(node.left) + size(node.right);
        return node;
    }

    public Key min() {
        if (isEmpty()) {
            throw new NoSuchElementException("calls min() with empty symbol table");
        }
        return min(root).key;
    }

    private Node min(Node node) {
        if (node.left == null) {
            return node;
        } else {
            return min(node.left);
        }
    }

    public Key max() {
        if (isEmpty()) {
            throw new NoSuchElementException("calls min() with empty symbol table");
        }
        return max(root).key;
    }

    private Node max(Node node) {
        if (node.right == null) {
            return node;
        } else {
            return max(node.left);
        }
    }

    public Key floor(Key key) {
        if (key == null) {
            throw new IllegalArgumentException("argument to floor() is null");
        }
        if (isEmpty()) {
            throw new NoSuchElementException("calls floor() with no empty symbol table");
        }
        Node node = floor(root, key);
        if (node == null) {
            throw new NoSuchElementException("argument to floor() is too small");
        }
        return node.key;
    }

    private Node floor(Node node, Key key) {
        if (node == null) {
            return null;
        }
        int cmp = key.compareTo(node.key);
        if (cmp == 0) {
            return node;
        } else if (cmp < 0) {
            return floor(node.left, key);
        }
        Node t = floor(node.right, key);
        if (t != null) {
            return t;
        } else {
            return node;
        }
    }

    public Key floor2(Key key) {
        Key floor = floor2(root, key, null);
        if (floor == null) {
            throw new NoSuchElementException("Argument to floor() is too small");
        } else {
            return floor;
        }
    }

    private Key floor2(Node node, Key key, Key champ) {
        if (node == null) {
            return champ;
        }
        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            return floor2(node.left, key, champ);
        } else if (cmp > 0) {
            return floor2(node.right, key, champ);
        } else {
            return node.key;
        }
    }

    public Key ceiling(Key key) {
        if (key == null) {
            throw new IllegalArgumentException("argument to ceiling() is null");
        }
        if (isEmpty()) {
            throw new NoSuchElementException("calls ceiling() with no empty symbol table");
        }
        Node node = ceiling(root, key);
        if (node == null) {
            throw new NoSuchElementException("argument to ceiling() is too small");
        }
        return node.key;
    }

    private Node ceiling(Node node, Key key) {
        if (node == null) {
            return null;
        }
        int cmp = key.compareTo(node.key);
        if (cmp == 0) {
            return node;
        } else if (cmp < 0) {
            Node t = ceiling(node.left, key);
            if (t != null) {
                return t;
            } else {
                return node;
            }
        }
        return ceiling(node.right, key);
    }

    public Key select(int rank) {
        if (rank < 0 || rank > size()) {
            throw new IllegalArgumentException("argument to select() is invalid: " + rank);
        }
        return select(root, rank);
    }

    private Key select(Node node, int rank) {
        if (node == null) {
            return null;
        }
        int leftSize = size(node.left);
        if (leftSize > rank) {
            return select(node.left, rank);
        } else if (leftSize < rank) {
            return select(node.right, rank - leftSize - 1);
        } else {
            return node.key;
        }
    }

    public int rank(Key key) {
        if (root == null) {
            return -1;
        }
        return rank(key, root);
    }

    private int rank(Key key, Node node) {
        if (node == null) {
            return 0;
        }
        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            return rank(key, node.left);
        } else if (cmp > 0) {
            return 1 + size(node.left) + rank(key, node.right);
        } else {
            return size(node.left);
        }
    }

    public int height() {
        return height(root);
    }

   private int height(Node node) {
        if (node == null) {
            return -1;
        }
        return 1 + Math.max(height(node.left), height(node.right));
   }
}
