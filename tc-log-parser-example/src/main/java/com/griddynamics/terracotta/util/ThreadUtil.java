package com.griddynamics.terracotta.util;

/**
 * @author agorbunov @ 07.05.2009 18:30:23
 */
public class ThreadUtil {

    public static void sleep(long delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
