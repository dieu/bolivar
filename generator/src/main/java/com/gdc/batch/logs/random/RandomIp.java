package com.gdc.batch.logs.random;

import java.util.Random;

/**
 * Created by vkatson
 * Date: 27.02.2009
 * Time: 11:19:49
 */
public class RandomIp extends RandomValue {

    protected String ipPattern = "%d.%d.%d.%d";

    public String getRandomValue() {
        return String.format(ipPattern, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    }
}

