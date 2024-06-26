package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {
    private int size;
    private DLLNode sentFront;
    private DLLNode sentBack;

    public LinkedListDeque() {
        sentFront = new DLLNode(null, null, null);
        sentBack = new DLLNode(null, null, null);
        sentFront.next = sentBack;
        sentBack.prev = sentFront;
        size = 0;
    }

    @Override
    public void addFirst(T item) {
        DLLNode node = new DLLNode(item, sentFront, sentFront.next);
        sentFront.next.prev = node;
        sentFront.next = node;
        size = size + 1;
    }

    @Override
    public void addLast(T item) {
        DLLNode node = new DLLNode(item, sentBack.prev, sentBack);
        sentBack.prev.next = node;
        sentBack.prev = node;
        size = size + 1;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        DLLNode node = sentFront.next;
        while (node != sentBack) {
            System.out.print(node.item);
            if (node.next == sentBack) {
                System.out.println("");
            } else {
                System.out.print(" ");
            }
            node = node.next;
        }
    }
    @Override
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }

        T item = sentFront.next.item;
        sentFront.next.next.prev = sentFront;
        sentFront.next = sentFront.next.next;
        size = size - 1;
        return item;
    }

    @Override
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }

        T item = sentBack.prev.item;
        sentBack.prev.prev.next = sentBack;
        sentBack.prev = sentBack.prev.prev;
        size = size - 1;
        return item;
    }

    @Override
    public T get(int index) {
        int k = 0;
        DLLNode node = sentFront.next;
        while (node != sentBack) {
            if (k == index) {
                return node.item;
            }
            k++;
            node = node.next;
        }
        return null;
    }

    public T getRecursive(int index) {
        return getRecursiveNode(sentFront.next, index);
    }

    private T getRecursiveNode(DLLNode node, int index) {
        if (index == 0 || node == sentBack) {
            return node.item;
        }
        return getRecursiveNode(node.next, index - 1);
    }

    @Override
    public Iterator<T> iterator() {
        return new LinkedListDequeIterator();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof Deque)) {
            return false;
        }

        Deque other = (Deque) obj;
        if (this.size() != other.size()) {
            return false;
        }

        int size = size();
        for (int i = 0; i < size; i++) {
            if (!this.get(i).equals(other.get(i))) {
                return false;
            }
        }
        return true;
        }

    private class LinkedListDequeIterator implements Iterator<T> {
        private DLLNode elem;
        public LinkedListDequeIterator() {
            elem = sentFront.next;
        }
        @Override
        public boolean hasNext() {
            return elem != sentBack;
        }

        @Override
        public T next() {
            T item = elem.item;
            elem = elem.next;
            return item;
        }
    }
    private class DLLNode {
        private T item;
        private DLLNode prev;
        private DLLNode next;
        private DLLNode(T i, DLLNode p, DLLNode n) {
            item = i;
            prev = p;
            next = n;
        }
    }
}
