package pathfinding.datastructures;

import java.util.NoSuchElementException;

/**
 * Custom Generic Binary Min-Heap Implementation.
 * Provides priority queue operations with O(log n) insertion and extraction.
 *
 * @param <T> Element type that implements Comparable<T>
 */
public class MinHeap<T extends Comparable<T>> {

    private static final int DEFAULT_CAPACITY = 32;
    private Object[] elements;
    private int size;

    public MinHeap() {
        this(DEFAULT_CAPACITY);
    }

    public MinHeap(int initialCapacity) {
        if (initialCapacity <= 0) {
            throw new IllegalArgumentException("Initial capacity must be positive: " + initialCapacity);
        }
        this.elements = new Object[initialCapacity];
        this.size = 0;
    }

    /**
     * Inserts an element into the min-heap.
     * Time Complexity: O(log n)
     */
    public void insert(T item) {
        if (item == null) {
            throw new IllegalArgumentException("Null items cannot be inserted into MinHeap.");
        }
        ensureCapacity();
        elements[size] = item;
        siftUp(size);
        size++;
    }

    /**
     * Retrieves and removes the minimum element (root).
     * Time Complexity: O(log n)
     */
    @SuppressWarnings("unchecked")
    public T extractMin() {
        if (isEmpty()) {
            throw new NoSuchElementException("Cannot extract from an empty MinHeap.");
        }
        T min = (T) elements[0];
        elements[0] = elements[size - 1];
        elements[size - 1] = null;
        size--;

        if (size > 0) {
            siftDown(0);
        }
        return min;
    }

    /**
     * Retrieves but does not remove the minimum element.
     * Time Complexity: O(1)
     */
    @SuppressWarnings("unchecked")
    public T peek() {
        if (isEmpty()) {
            throw new NoSuchElementException("Cannot peek into an empty MinHeap.");
        }
        return (T) elements[0];
    }

    /**
     * Checks if the heap contains the specified element.
     * Time Complexity: O(n)
     */
    public boolean contains(T item) {
        if (item == null) return false;
        for (int i = 0; i < size; i++) {
            if (elements[i].equals(item)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Re-heapifies the structure when an item's priority/cost has decreased.
     * Time Complexity: O(n) lookup + O(log n) sift-up
     */
    public void reHeapify(T item) {
        if (item == null) return;
        for (int i = 0; i < size; i++) {
            if (elements[i].equals(item)) {
                siftUp(i);
                siftDown(i);
                return;
            }
        }
    }

    /**
     * Deletes a specific item from the heap.
     * Demonstrates heap deletion operation.
     * Time Complexity: O(n) search + O(log n) sift
     */
    @SuppressWarnings("unchecked")
    public boolean delete(T item) {
        if (item == null || isEmpty()) return false;
        for (int i = 0; i < size; i++) {
            if (elements[i].equals(item)) {
                elements[i] = elements[size - 1];
                elements[size - 1] = null;
                size--;
                if (i < size) {
                    siftUp(i);
                    siftDown(i);
                }
                return true;
            }
        }
        return false;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void clear() {
        for (int i = 0; i < size; i++) {
            elements[i] = null;
        }
        size = 0;
    }

    // --- Internal Heap Helper Methods ---

    @SuppressWarnings("unchecked")
    private void siftUp(int index) {
        T target = (T) elements[index];
        while (index > 0) {
            int parentIndex = (index - 1) / 2;
            T parent = (T) elements[parentIndex];
            if (target.compareTo(parent) >= 0) {
                break;
            }
            elements[index] = parent;
            index = parentIndex;
        }
        elements[index] = target;
    }

    @SuppressWarnings("unchecked")
    private void siftDown(int index) {
        T target = (T) elements[index];
        int half = size / 2; // Loop while index has at least one child
        while (index < half) {
            int leftChild = 2 * index + 1;
            int rightChild = leftChild + 1;
            int bestChild = leftChild;

            if (rightChild < size && ((T) elements[rightChild]).compareTo((T) elements[leftChild]) < 0) {
                bestChild = rightChild;
            }

            if (target.compareTo((T) elements[bestChild]) <= 0) {
                break;
            }

            elements[index] = elements[bestChild];
            index = bestChild;
        }
        elements[index] = target;
    }

    private void ensureCapacity() {
        if (size == elements.length) {
            int newCapacity = elements.length * 2;
            Object[] newElements = new Object[newCapacity];
            System.arraycopy(elements, 0, newElements, 0, elements.length);
            elements = newElements;
        }
    }
}
