package com.griddynamics.terracotta.parser.helpers.impl;

import com.griddynamics.terracotta.parser.helpers.Statistics;

import java.util.concurrent.ConcurrentMap;
import java.util.Map;

import org.terracotta.modules.concurrent.collections.ConcurrentStringMap;

/**
 * @author: apanasenko aka dieu
 * Date: 03.06.2009
 * Time: 14:21:05
 */
public class Monolitical implements Statistics {
       private ConcurrentMap<String, Long> whole = new ConcurrentStringMap<Long>();

       public synchronized void add(Map<String, Long> part) {
           for (String ip : part.keySet()) {
               Long usage = part.get(ip);
               increaseTraffic(ip, usage);
           }
       }

       private void increaseTraffic(String ip, Long usage) {
           whole.putIfAbsent(ip, 0L);
           whole.put(ip, whole.get(ip) + usage);
       }

       public Map<String, Long> merge() {
           return whole;
       }
   }

