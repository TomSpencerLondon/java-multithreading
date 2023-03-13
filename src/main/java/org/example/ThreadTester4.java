package org.example;

public class ThreadTester4 {
    public static void main(String[] args) {
        System.out.println("main is starting");
        Thread thread = new Thread(() -> {
            System.out.println(Thread.currentThread());
        }, "Our thread");

        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("main is exiting");
    }
}
