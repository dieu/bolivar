package com.griddynamics.terracotta.parser.separate_downloading;

import commonj.work.Work;

import java.lang.reflect.Constructor;

import static com.griddynamics.terracotta.util.ErrUtil.runtimeException;

/**
 * Instantiates a work item not by the master, but by the worker
 * when it picks up the item off the queue.
 * This helps preventing UnlockedSharedObjectException.
 * http://www.shinetech.com/pages/viewpage.action?pageId=1276
 *
 * @author agorbunov @ 13.05.2009 13:31:33
 */
public class LocalWork implements Work {
    private Class<? extends Work> work;
    private Object[] arguments;

    public static Work createLocally(Class<? extends Work> work, Object... arguments) {
        return new LocalWork(work, arguments);
    }

    public LocalWork(Class<? extends Work> work, Object... arguments) {
        this.work = work;
        this.arguments = arguments;        
    }

    public void run() {
        try {
            createAndRunWork();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void createAndRunWork() throws Exception {
        run(createWork());
    }

    private Work createWork() throws Exception {
        return constructor().newInstance(arguments);
    }

    private Constructor<? extends Work> constructor() throws Exception {
        Class[] argumentTypes = new Class[arguments.length];
        for (int i = 0; i < arguments.length; i++)
            argumentTypes[i] = arguments[i].getClass();
        return work.getConstructor(argumentTypes);
    }

    private void run(Work work) {
        reportStarted();
        try {
            work.run();
        } catch (Throwable e) {
            reportFailed(e);
            rethrow(e);
        }
        reportFinished();
    }

    protected void reportStarted() {
        // Default implementation.
    }

    protected void reportFailed(Throwable e) {
        // Default implementation.
    }

    private void rethrow(Throwable e) {
        throw runtimeException(e);
    }

    protected void reportFinished() {
        // Default implementation.
    }

    public String toString() {
        return work.toString();
    }

    public int hashCode() {
        return work.hashCode();
    }

    public boolean equals(Object o) {
        return work.equals(o);
    }

    public boolean isDaemon() {
        return false;
    }

    public void release() {
        // Do nothing.
    }        
}
