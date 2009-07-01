package com.griddynamics.terracotta.helpers;

/**
 * @author apanasenko aka dieu
 *         Date: 25.06.2009
 *         Time: 13:37:04
 */
public class Pipe<T> {
    private T pipes;
    private long time;    

    public Pipe(T pipes) {
        this.pipes = pipes;
    }

    public T getPipe() {
        return pipes;
    }

    public void setPipes(T pipes) {
        this.pipes = pipes;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}

