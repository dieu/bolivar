package com.griddynamics.equestrian.helpers;

/**
 * @author: apanasenko aka dieu
 * Date: 06.05.2009
 * Time: 13:37:01
 */
public class ApplicationPath {
    public static String APPLICATION_PATH;
    public static String CAPISTRANO_PATH;
    public static String HOST_LOG_PATH;

    static {
        APPLICATION_PATH = System.getenv("BOLIVAR_HOME");
        CAPISTRANO_PATH = System.getenv("CAP_HOME");
        HOST_LOG_PATH = APPLICATION_PATH + "host.xml";
    }

    public static void main(String[] arg) {
        new ApplicationPath();
    }
}
