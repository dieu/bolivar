package com.gdc.batch.logs.random;

import java.util.Random;

/**
 * Created by vkatson
 * Date: 27.02.2009
 * Time: 11:22:52
 */
public abstract class RandomValue {
    protected int length = 5;

    public void setLength(int length) {
        this.length = length;
    }

    protected Random rnd = new Random();

    public abstract String getRandomValue();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RandomValue)) return false;

        RandomValue that = (RandomValue) o;

        return length == that.length;
    }

    @Override
    public int hashCode() {
        int result = length;
        result = 31 * result + (rnd != null ? rnd.hashCode() : 0);
        return result;
    }
}
