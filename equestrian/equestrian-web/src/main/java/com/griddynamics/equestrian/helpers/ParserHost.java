package com.griddynamics.equestrian.helpers;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.util.List;
import java.util.Map;
import java.io.IOException;

/**
 * @author: apanasenko aka dieu
 * Date: 08.05.2009
 * Time: 14:09:21
 */
public interface ParserHost {
    int parse(int n) throws Exception;
    int getCountNode() throws Exception;
    void clear();
    Map<String, Boolean> getNodeIp();
}
