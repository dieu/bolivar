package com.gdc.batch.logs.random;

/**
 * Created by vkatson
 * Date: 27.02.2009
 * Time: 12:11:27
 */
public class RandomNumber extends RandomValue {

    public RandomNumber() {
        super();
    }

    public RandomNumber(int length) {
        super();
        this.length = length;
    }

    public String getRandomValue() {
        return String.valueOf(rnd.nextInt((int) Math.pow(10, length)));
    }
}
