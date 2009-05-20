package com.gdc.batch.logs.random;

import com.gdc.batch.logs.config.Config;

/**
 * Created by vkatson
 * Modified by agorbunov
 * Date: 14.05.2009
 * Time: 17:44:43
 */
public class RandomLocalIp extends RandomIp {
    private int subnets;
    private int usersPerSubnet;

    public RandomLocalIp() {
        LocalIpConfig config = new LocalIpConfig();
        subnets = config.subnets();
        usersPerSubnet = config.usersPerSubnet();
    }
    
    public String getRandomValue() {
        return String.format(ipPattern, 192, 168, rnd.nextInt(subnets), rnd.nextInt(usersPerSubnet));
    }

    private class LocalIpConfig {
        public int subnets() {
            return readValueWithDefault("localIp.subnets", 32);
        }

        public int usersPerSubnet() {
            return readValueWithDefault("localIp.usersPerSubnet", 64);
        }

        private int readValueWithDefault(String key, int defaultValue) {
            String value = Config.getProperty(key);
            if (value == null)
                return defaultValue;
            return Integer.parseInt(value);
        }
    }
}
