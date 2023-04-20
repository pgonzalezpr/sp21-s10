package bstmap;

import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {
    private BSTNode root;
    private int size = 0;

    private class BSTNode {
        private K key;
        private V value;
        private BSTNode left;
        private BSTNode right;
        private int size;

        private BSTNode(K key, V value) {
            this.key = key;
            this.value = value;
            this.size = 0;
        }
        @Override
        public String toString() {
            return "<" + key.toString() + ", " + value.toString() + ">";
        }
    }
    public void clear() {
        root = null;
        size = 0;
    }

    @Override
    public boolean containsKey(K key) {
        return containsKey(root, key);
    }

    private boolean containsKey(BSTNode node, K key) {
        if (node == null) {
            return false;
        }

        if (key.compareTo(node.key) > 0) {
            return containsKey(node.right, key);
        } else if (key.compareTo(node.key) < 0) {
            return containsKey(node.left, key);
        }

        return true;
    }

    @Override
    public V get(K key) {
        return get(root, key);
    }

    private V get(BSTNode node, K key) {
        if (node == null) {
            return null;
        }

        if (key.compareTo(node.key) > 0) {
            return get(node.right, key);
        } else if (key.compareTo(node.key) < 0) {
            return get(node.left, key);
        }

        return node.value;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        root = insert(root, key, value);
        size = root.size + 1;
    }

    private BSTNode insert(BSTNode node, K key, V value) {
        if (node == null) {
            return new BSTNode(key, value);
        }

        if (key.compareTo(node.key) > 0) {
            node.right = insert(node.right, key, value);
            node.size++;
        } else if (key.compareTo(node.key) < 0) {
            node.left = insert(node.left, key, value);
            node.size++;
        }

        node.value = value;
        return node;
    }

    public void printInOrder() {
        printInOrder(root);
    }

    private void printInOrder(BSTNode node) {
        if (node == null) {
            return;
        }
        printInOrder(node.left);
        System.out.println(node);
        printInOrder(node.right);
    }

    @Override
    public Set<K> keySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException();
    }
}
