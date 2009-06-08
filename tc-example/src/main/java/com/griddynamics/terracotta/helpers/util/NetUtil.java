package com.griddynamics.terracotta.helpers.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author agorbunov @ 27.05.2009 14:48:13
 */
public class NetUtil {

    public static String worker() {
        return host();
    }

    public static String host() {
        return first(hostAndIp());
    }

    public static String ip() {
        return second(hostAndIp());
    }

    private static String hostAndIp() {
        try {
            return localAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    private static String localAddress() throws UnknownHostException {
        return InetAddress.getLocalHost().toString();
    }

    public static String first(String s) {
        return splitBySlash(s)[0];
    }

    private static String second(String s) {
        return splitBySlash(s)[1];
    }

    private static String[] splitBySlash(String s) {
        return s.split("/");
    }
}
