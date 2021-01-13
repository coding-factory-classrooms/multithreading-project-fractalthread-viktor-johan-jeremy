package org.example;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class LRUCache<K, V> {
    private Node<K, V> lru;
    private Node<K, V> mru;
    private final Map<K, Node<K, V>> container;
    private final int capacity;
    private int currentSize;

<<<<<<< Updated upstream
    private final ReentrantLock lock = new ReentrantLock();
=======
    private final static ReentrantLock lock = new ReentrantLock();
>>>>>>> Stashed changes

    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.currentSize = 0;
        lru = new Node<K, V>(null, null, null, null);
        mru = lru;
        container = new HashMap<K, Node<K, V>>();
    }

    public V get(K key) {
        lock.lock();
        Node<K, V> tempNode = container.get(key);
        if (tempNode == null) {
            lock.unlock();
            return null;
        }
        // If MRU leave the list as it is
        else if (tempNode.key == mru.key) {
            lock.unlock();
            return mru.value;
        }

        // Get the next and prev nodes
        Node<K, V> nextNode = tempNode.next;
        Node<K, V> prevNode = tempNode.prev;

        // If at the left-most, we update LRU
        if (tempNode.key == lru.key) {
            nextNode.prev = null;
            lru = nextNode;
        }

        // If we are in the middle, we need to update the items before and after our
        // item
        else if (tempNode.key != mru.key) {
            prevNode.next = nextNode;
            nextNode.prev = prevNode;
        }

        // Finally move our item to the MRU
        tempNode.prev = mru;
        mru.next = tempNode;
        mru = tempNode;
        mru.next = null;

        lock.unlock();
        return tempNode.value;

    }

    public void put(K key, V value) {
        lock.lock();
        if (container.containsKey(key)) {
            lock.unlock();
            return;
        }

        // Put the new node at the right-most end of the linked-list
        Node<K, V> myNode = new Node<K, V>(mru, null, key, value);
        mru.next = myNode;
        container.put(key, myNode);
        mru = myNode;

        // Delete the left-most entry and update the LRU pointer
        if (currentSize == capacity) {
            container.remove(lru.key);
            lru = lru.next;
            lru.prev = null;
        }

        // Update container size, for the first added entry update the LRU pointer
        else if (currentSize < capacity) {
            if (currentSize == 0) {
                lru = myNode;
            }
            currentSize++;
        }
        lock.unlock();
    }

    // Node for doubly linked list
    class Node<T, U> {
        T key;
        U value;
        Node<T, U> prev;
        Node<T, U> next;

        public Node(Node<T, U> prev, Node<T, U> next, T key, U value) {
            this.prev = prev;
            this.next = next;
            this.key = key;
            this.value = value;
        }
    }
}
