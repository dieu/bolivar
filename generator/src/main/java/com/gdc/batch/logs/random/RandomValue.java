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

}
