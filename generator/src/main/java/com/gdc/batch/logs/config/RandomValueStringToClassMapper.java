package com.gdc.batch.logs.config;

import com.gdc.batch.logs.random.RandomValue;
import com.gdc.batch.logs.random.RandomNumber;
import com.gdc.batch.logs.random.RandomIp;
import com.gdc.batch.logs.random.RandomLocalIp;

/**
 * Created by vkatson
 * Date: 03.03.2009
 * Time: 16:18:35
 */
public class RandomValueStringToClassMapper extends StringToClassMapper<RandomValue> {

    @Override
    public RandomValue getInstanceByKey(String classKey) throws InstantiationException, IllegalAccessException {
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

    @Override
    protected void loadClassMapping() {
        classMap.put("d", RandomNumber.class);
        classMap.put("ip", RandomIp.class);
        classMap.put("localIp", RandomLocalIp.class);
    }
}
