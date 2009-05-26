package com.griddynamics.terracotta.util;

/**
 * @author agorbunov @ 26.05.2009 18:09:34
 */
public class ErrUtil {

    public static RuntimeException runtimeException(Throwable e) {
        if (e instanceof RuntimeException)
            return (RuntimeException) e;
        else
            return new RuntimeException(e);
    }
}
