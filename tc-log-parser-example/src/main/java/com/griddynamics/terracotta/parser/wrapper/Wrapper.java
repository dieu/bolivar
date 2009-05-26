package com.griddynamics.terracotta.parser.wrapper;

import commonj.work.Work;

/**
 * Instantiates a work item not by the master, but by the worker
 * when it picks up the item off the queue.
 * This helps preventing UnlockedSharedObjectException.
 * http://www.shinetech.com/pages/viewpage.action?pageId=1276
 *
 * @author agorbunov @ 13.05.2009 13:31:33
 */
public abstract class Wrapper implements Work {
    private Factory factory;
    private Class<? extends Work> work;

    public Wrapper(Class<? extends Work> work, Object... arguments) {
        factory = new Factory(work, arguments);
        this.work = work;
    }

    public void run() {
        try {
            createAndRunWork();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void createAndRunWork() throws Exception {
        Work work = createWork();
        run(work);
    }

    private Work createWork() throws Exception {
        return factory.createWork();
    }

    protected void run(Work work) {
        work.run();
    }

    public String toString() {
        return work.toString();
    }

    public boolean isDaemon() {
        return false;
    }

    public void release() {
        // Do nothing.
    }
}
