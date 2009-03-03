package com.gdc.batch.logs.config;

import java.util.Map;
import java.util.HashMap;

/**
 * Created by vkatson
 * Date: 03.03.2009
 * Time: 16:15:21
 */
abstract public class AbstractStringToClassMapper<T> {
    Map<String, Class> classMap = new HashMap<String, Class>();

    protected AbstractStringToClassMapper() {
        loadClassMapping();
    }

    public abstract T getInstanceByKey(String classKey) throws InstantiationException, IllegalAccessException;

    protected abstract void loadClassMapping();
}
