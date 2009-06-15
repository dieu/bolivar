package com.gdc.batch.logs.config;

import com.gdc.batch.logs.random.RandomValue;
import com.gdc.batch.logs.config.Config;

import java.util.List;
import java.util.ArrayList;

/**
 * Created by vkatson
 * Date: 27.02.2009
 * Time: 17:16:51
 */
public class ConfigFormatParser {

    StringToClassMapper<RandomValue> stringToClassMapper = new RandomValueStringToClassMapper();

    public List<RandomValue> parseConfig() throws InstantiationException, IllegalAccessException {
        List<RandomValue> res = new ArrayList<RandomValue>();
        String confString = Config.getProperty("format");
        String[] tokens = confString.split(" ");
        for (String classKey : tokens) {
            res.add(stringToClassMapper.getInstanceByKey(classKey));
        }
        return res;
    }
}
