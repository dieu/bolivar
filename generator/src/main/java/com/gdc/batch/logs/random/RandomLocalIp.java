package com.gdc.batch.logs.random;

import com.gdc.batch.logs.config.Config;

/**
 * Created by vkatson
 * Modified by agorbunov
 * Date: 14.05.2009
 * Time: 17:44:43
 */
public class RandomLocalIp extends RandomIp {
    private int rooms;
    private int usersPerRoom;

    public RandomLocalIp() {
        LocalIpConfig config = new LocalIpConfig();
        rooms = config.rooms();
        usersPerRoom = config.usersPerRoom();
    }
    
    public String getRandomValue() {
        return String.format(ipPattern, 192, 168, rnd.nextInt(rooms), rnd.nextInt(usersPerRoom));
    }

    private class LocalIpConfig {
        public int rooms() {
            return readIntWithDefault("localIp.rooms", 32);
        }

        public int usersPerRoom() {
            return readIntWithDefault("localIp.usersPerRoom", 64);
        }

        private int readIntWithDefault(String property, int defaultValue) {
            String value = Config.getProperty(property);
            if (value == null)
                return defaultValue;
            return Integer.parseInt(value);
        }
    }
}
