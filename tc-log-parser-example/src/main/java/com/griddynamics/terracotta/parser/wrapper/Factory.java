package com.griddynamics.terracotta.parser.wrapper;

import commonj.work.Work;

import java.lang.reflect.Constructor;

/**
 * @author agorbunov @ 26.05.2009 20:46:37
 */
public class Factory {
    private Class<? extends Work> work;
    private Object[] arguments;

    public Factory(Class<? extends Work> work, Object... arguments) {
        this.work = work;
        this.arguments = arguments;
    }

    public Work createWork() throws Exception {
        return constructor().newInstance(arguments);
    }

    private Constructor<? extends Work> constructor() throws Exception {
        return work.getConstructor(types());
    }

    private Class[] types() {
        Class[] types = new Class[arguments.length];
        for (int i = 0; i < arguments.length; i++)
            types[i] = arguments[i].getClass();
        return types;
    }
}
