package org.example;

public class ThreadTester1 {
    public static void main(String[] args) {
        System.out.println("main is starting");
        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                System.out.println(Thread.currentThread() + ", " + i);
            }
        }, "thread2");

        thread2.start();

        System.out.println("main is exiting");

        new Thread().run();

    }
}