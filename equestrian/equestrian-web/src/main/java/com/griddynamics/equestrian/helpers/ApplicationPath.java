package com.griddynamics.equestrian.helpers;

/**
 * @author: apanasenko aka dieu
 * Date: 06.05.2009
 * Time: 13:37:01
 */
public class ApplicationPath {
    public static String APPLICATION_PATH;
    public static String CAPISTRANO_PATH;

    static {
        APPLICATION_PATH = System.getenv("BOLIVAR_HOME");
        CAPISTRANO_PATH = System.getenv("CAP_HOME");
    }
}
