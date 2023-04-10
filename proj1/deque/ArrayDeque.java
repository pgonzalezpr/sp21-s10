package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T>, Iterable<T> {
    private T[] items;
    private int size;
    private int nextFirst;
    private int nextLast;
    public ArrayDeque() {
        items = (T[]) new Object[8];
        size = 0;
        nextFirst = 4;
        nextLast = 5;
    }

    @Override
    public void addFirst(T item) {
        if (items[nextFirst] != null) {
            resize(items.length * 2);
        }

        items[nextFirst] = item;
        nextFirst--;
        size++;

        if (nextFirst < 0) {
            nextFirst = items.length - 1;
        }
    }

    private void resize(int capacity) {
        T[] newItems = (T[]) new Object[capacity];

        int rightElements = 0;
        for (int i = lastFirstPos(); i < items.length && items[i] != null; i++) {
            rightElements++;
            i++;
        }

        int newItemsPos = newItems.length - rightElements;
        for (T item : this) {
            newItems[newItemsPos] = item;
            newItemsPos++;

            if (newItemsPos == newItems.length) {
                newItemsPos = 0;
            }
        }

        items = newItems;
        nextFirst = newItems.length - rightElements - 1;
        nextLast = newItemsPos;
    }

    @Override
    public void addLast(T item) {
        if (items[nextLast] != null) {
            resize(items.length * 2);
        }

        items[nextLast] = item;
        nextLast++;
        size++;

        if (nextLast == items.length) {
            nextLast = 0;
        }
    }


    private int lastFirstPos() {
        if (nextFirst + 1 == items.length) {
            return 0;
        } else {
            return nextFirst + 1;
        }
    }

    private int lastLastPos() {
        if (nextLast - 1 < 0) {
            return items.length - 1;
        } else {
            return nextLast - 1;
        }
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }

        if (1.0 * (size - 1) / items.length < 0.25 && items.length > 8) {
            resize(items.length / 2);
        }

        T item = items[lastFirstPos()];
        items[lastFirstPos()] = null;
        nextFirst = lastFirstPos();
        size--;

        return item;
    }

    @Override
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }

        if (1.0 * (size - 1) / items.length < 0.25 && items.length > 8) {
            resize(items.length / 2);
        }

        T item = items[lastLastPos()];
        items[lastLastPos()] = null;
        nextLast = lastLastPos();
        size--;

        return item;
    }

    @Override
    public T get(int index) {
        if (index >= size) {
            return null;
        }
        int getPosition = lastFirstPos() + index;
        if (getPosition >= items.length) {
            getPosition = getPosition - items.length;
        }
        return items[getPosition];
    }

    @Override
    public void printDeque() {
        int index = 0;

        while (index < size) {
            System.out.print(get(index));
            if (index == size - 1) {
                System.out.println("");
            } else {
                System.out.print(" ");
            }
            index++;
        }
    }

    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ArrayDeque other) {
            if (this.size() != other.size()) {
                return false;
            }

            int index = 0;
            while (index < this.size()) {
                if (!this.get(index).equals(other.get(index))) {
                    return false;
                }
                index++;
            }
            return true;
        }
        return false;
    }

    private class ArrayDequeIterator implements Iterator<T> {
        private int index;
        public ArrayDequeIterator() {
            index = 0;
        }
        @Override
        public boolean hasNext() {
            return index < size;
        }
        @Override
        public T next() {
            T item = get(index);
            index++;
            return item;
        }
    }
}
