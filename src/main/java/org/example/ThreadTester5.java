package org.example;

public class ThreadTester5 {

    public static void main(String[] args) {
        String lock1 = "lock1";
        String lock2 = "lock2";

        Thread thread1 = new Thread(() -> {
            synchronized (lock1) {

                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (lock2) {
                    System.out.println("lock acquired");
                }
            }
        }, "thread1");

        Thread thread2 = new Thread(() -> {
            synchronized (lock1) {

                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (lock2) {
                    System.out.println("lock acquired");
                }
            }
        }, "thread2");

        thread1.start();
        thread2.start();
    }
}
