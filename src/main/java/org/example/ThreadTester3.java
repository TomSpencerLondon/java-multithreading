package org.example;

public class ThreadTester3 {
    public static void main(String[] args) {
        Thread states = new Thread(() -> {
            try {
                Thread.sleep(1);
                for (int i = 1000; i > 0; i--) {

                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, "States");

        states.start();

        while (true) {
            Thread.State state = states.getState();
            System.out.println(state);
            if (state == Thread.State.TERMINATED) {
                break;
            }
        }
    }
}
