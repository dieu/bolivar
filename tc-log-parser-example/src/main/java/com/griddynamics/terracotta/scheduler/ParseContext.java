package com.griddynamics.terracotta.scheduler;

import com.griddynamics.terracotta.parser.Aggregator;

/**
 * @author: apanasenko aka dieu
 * Date: 03.06.2009
 * Time: 16:40:57
 */
public class ParseContext {
    private Aggregator aggregator = new Aggregator();
    private String workerDir = "";
    
    public Aggregator getAggregator() {
        return aggregator;
    }

    public void setAggregator(Aggregator aggregator) {
        this.aggregator = aggregator;
    }

    public String getWorkerDir() {
        return workerDir;
    }

    public void setWorkerDir(String workerDir) {
        this.workerDir = workerDir;
    }
}
