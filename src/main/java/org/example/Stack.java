package org.example;

public class Stack {
    private int[] array;
    private int stackTop;

    Object lock;
    public Stack(int capacity) {
        array = new int[capacity];
        stackTop = -1;

        lock = new Object();
    }

    public boolean isEmpty() {
        return stackTop < 0;
    }

    public boolean isFull() {
        return stackTop >= array.length - 1;
    }

    // t1, t2, t3
    public synchronized boolean push(int element) {
            if (isFull()) {
                return false;
            }
            ++stackTop;

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            array[stackTop] = element;

            return true;
    }

    // t1, t4, t5
    public synchronized int pop() {
            if (isEmpty()) {
                return Integer.MIN_VALUE;
            }

            int result = array[stackTop];
            array[stackTop] = Integer.MIN_VALUE;

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            stackTop--;
            return result;
    }
}
