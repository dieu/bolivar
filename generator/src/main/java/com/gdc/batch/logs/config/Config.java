/**
 * Created by vkatson
 * Date: 27.02.2009
 * Time: 11:01:01
 */
package com.gdc.batch.logs.config;

import java.util.Properties;
import java.io.IOException;

public class Config {
    private static final Config instance = new Config();
    private Properties properties;

    private Config() {
        properties = new Properties();
        try {
            properties.load(ClassLoader.getSystemResourceAsStream("generator.properties"));
        } catch (IOException e) {
            System.out.println("Can't read properties file.");
        }
    }

    public static String getProperty(String key){
        return instance.properties.getProperty(key);
    }
}

