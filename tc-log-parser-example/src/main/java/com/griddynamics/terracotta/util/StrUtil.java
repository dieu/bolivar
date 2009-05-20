package com.griddynamics.terracotta.util;

/**
 * @author agorbunov @ 12.05.2009 17:06:56
 */
public class StrUtil {
    
    public static String arrayToString(String[] a) {
        StringBuffer r = new StringBuffer();
        r.append("[");
        for (String s : a)
            r.append(s).append(" ");
        r.append("]");
        return r.toString();
    }

    public static String join(int[] items, String separator) {
        StringBuffer r = new StringBuffer();
        for (int item : items)
            r.append(item).append(separator);
        if (r.length() > 0)
            r.delete(r.length() - separator.length() - 1, r.length() - 1);
        return r.toString();
    }

    public static String surroundWithTag(Long num, String tag) {
        return surroundWithTag(num.toString(), tag);
    }

    public static String surroundWithTag(String s, String tag) {
        return "<" + tag + ">" + s + "</" + tag + ">";
    }
}
