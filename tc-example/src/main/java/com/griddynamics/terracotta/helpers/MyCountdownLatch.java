package com.griddynamics.terracotta.helpers;

/**
 * @author: apanasenko aka dieu
 * Date: 03.06.2009
 * Time: 18:28:47
 */
public class MyCountdownLatch {
    int count = -1;

    public MyCountdownLatch(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public synchronized void countDown() {
        count--;
        if (count == 0) {
            notifyAll();
        }
    }

    public synchronized void reset(int count) {
        this.count = count;
    }

    public synchronized void await() throws InterruptedException {
        if (count == 0) {
            notifyAll();
            return;
        } else {
            while (count > 0) {
                wait();
            }
        }
    }
}

