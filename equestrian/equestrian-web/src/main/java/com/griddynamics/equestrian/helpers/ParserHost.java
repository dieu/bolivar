package com.griddynamics.equestrian.helpers;

import java.util.List;
import java.util.Map;

/**
 * @author: apanasenko aka dieu
 * Date: 08.05.2009
 * Time: 14:09:21
 */
public interface ParserHost {
    int parse(int n) throws Exception;
    int parse() throws Exception;
    int getCountNode() throws Exception;
    void clear();
    Map<String, String> getNodeIp();
    String getNodeInfo();
    int getSchedulerSize();
    public int getServerSize();
}
