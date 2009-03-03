package com.gdc.batch.logs.config;

import com.gdc.batch.logs.random.RandomValue;
import com.gdc.batch.logs.random.RandomNumber;
import com.gdc.batch.logs.random.RandomIp;
import com.gdc.batch.logs.random.RandomLocalIp;
import com.gdc.batch.logs.config.Config;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by vkatson
 * Date: 27.02.2009
 * Time: 17:16:51
 */
public class ConfigFormatParser {

    Map<String, Class> classMap = new HashMap<String, Class>();

    public ConfigFormatParser() {
        init();
    }

    public List<RandomValue> parseConfig() throws InstantiationException, IllegalAccessException {
        List<RandomValue> res = new ArrayList<RandomValue>();
        String confString = Config.getInstance().getProperties().getProperty("format");
        String[] tokens = confString.split(" ");
        for (String classKey : tokens) {
            res.add(getRandomInstance(classKey));
        }
        return res;
    }

    private RandomValue getRandomInstance(String classKey) throws IllegalAccessException, InstantiationException {
        Class classOfRandom = classMap.get(classKey);
        int lenght = 5;
        if (classOfRandom == null) {
            classOfRandom = classMap.get(classKey.substring(0, classKey.length() - 1));
            lenght = Integer.valueOf(classKey.charAt(classKey.length() - 1) + "");
        }
        RandomValue res = (RandomValue) classOfRandom.newInstance();
        res.setLength(lenght);
        return res;
    }

    private void init() {
        classMap.put("d", RandomNumber.class);
        classMap.put("ip", RandomIp.class);
        classMap.put("localIp", RandomLocalIp.class);
    }
}
