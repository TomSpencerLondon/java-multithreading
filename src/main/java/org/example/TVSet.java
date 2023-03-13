package org.example;

public class TVSet {
    private static volatile TVSet tvSet = null;

    private TVSet() {
        System.out.println("TV set instantiated");
    }

    public static TVSet getTvSet() {
        if (tvSet == null) {
            synchronized (TVSet.class) {
                if (tvSet == null) {
                    tvSet = new TVSet();
                }
            }
        }

        return tvSet;
    }
}
