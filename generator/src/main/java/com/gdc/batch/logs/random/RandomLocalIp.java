package com.gdc.batch.logs.random;

/**
 * Created by vkatson
 * Date: 27.02.2009
 * Time: 17:44:43
 */
public class RandomLocalIp extends RandomIp {

    public String getRandomValue() {
        return String.format(ipPattern, 192, 168, rnd.nextInt(32), rnd.nextInt(64));
    }

}
