package org.example;

import java.util.LinkedList;
import java.util.Queue;

public class BlockingQueue {
    private Queue<Integer> queue;

    int capacity;

    private BlockingQueue(int capacity) {
        this.capacity = capacity;
        this.queue = new LinkedList<>();
    }

    public boolean add(int item) {
        synchronized (queue) {
            while (queue.size() == capacity) {
                try {
                    queue.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            queue.add(item);
            queue.notifyAll();

            return true;
        }
    }

    public int remove() {
        synchronized (queue) {
            while (queue.size() == 0) {
                // do something
                try {
                    queue.wait();



                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            int element = queue.poll();
            queue.notifyAll();
            return element;
        }
    }
}
