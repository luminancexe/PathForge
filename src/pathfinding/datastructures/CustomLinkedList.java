package pathfinding.datastructures;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Custom Generic Singly Linked List Implementation.
 * Provides O(1) prepend, append, and traversal operations.
 *
 * @param <T> Element type
 */
public class CustomLinkedList<T> implements Iterable<T> {

    public static class ListNode<E> {
        public E data;
        public ListNode<E> next;

        public ListNode(E data) {
            this.data = data;
            this.next = null;
        }
    }

    private ListNode<T> head;
    private ListNode<T> tail;
    private int size;

    public CustomLinkedList() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    /**
     * Inserts an element at the beginning of the list.
     * Time Complexity: O(1)
     * Crucial for path reconstruction: enables prepending nodes from target back to start.
     */
    public void addFirst(T data) {
        ListNode<T> newNode = new ListNode<>(data);
        if (isEmpty()) {
            head = newNode;
            tail = newNode;
        } else {
            newNode.next = head;
            head = newNode;
        }
        size++;
    }

    /**
     * Appends an element to the end of the list.
     * Time Complexity: O(1)
     */
    public void addLast(T data) {
        ListNode<T> newNode = new ListNode<>(data);
        if (isEmpty()) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            tail = newNode;
        }
        size++;
    }

    /**
     * Removes and returns the first element.
     * Time Complexity: O(1)
     */
    public T removeFirst() {
        if (isEmpty()) {
            throw new NoSuchElementException("Cannot remove from an empty list.");
        }
        T data = head.data;
        head = head.next;
        size--;
        if (isEmpty()) {
            tail = null;
        }
        return data;
    }

    /**
     * Removes and returns the last element.
     * Time Complexity: O(n) in singly linked list
     */
    public T removeLast() {
        if (isEmpty()) {
            throw new NoSuchElementException("Cannot remove from an empty list.");
        }
        if (head == tail) {
            T data = head.data;
            head = null;
            tail = null;
            size--;
            return data;
        }
        ListNode<T> current = head;
        while (current.next != tail) {
            current = current.next;
        }
        T data = tail.data;
        tail = current;
        tail.next = null;
        size--;
        return data;
    }

    /**
     * Removes the first occurrence of the specified element.
     * Time Complexity: O(n)
     */
    public boolean remove(T data) {
        if (isEmpty()) return false;
        if (head.data != null && head.data.equals(data)) {
            removeFirst();
            return true;
        }
        ListNode<T> current = head;
        while (current.next != null) {
            if (current.next.data != null && current.next.data.equals(data)) {
                if (current.next == tail) {
                    tail = current;
                }
                current.next = current.next.next;
                size--;
                return true;
            }
            current = current.next;
        }
        return false;
    }

    /**
     * Gets an element at a 0-indexed position.
     * Time Complexity: O(n)
     */
    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        ListNode<T> current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current.data;
    }

    public T getFirst() {
        if (isEmpty()) {
            throw new NoSuchElementException("List is empty.");
        }
        return head.data;
    }

    public T getLast() {
        if (isEmpty()) {
            throw new NoSuchElementException("List is empty.");
        }
        return tail.data;
    }

    public boolean contains(T data) {
        ListNode<T> current = head;
        while (current != null) {
            if (current.data != null && current.data.equals(data)) {
                return true;
            }
            current = current.next;
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
        head = null;
        tail = null;
        size = 0;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private ListNode<T> current = head;

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public T next() {
                if (current == null) {
                    throw new NoSuchElementException();
                }
                T val = current.data;
                current = current.next;
                return val;
            }
        };
    }
}
