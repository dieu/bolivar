package com.griddynamics.terracotta.helpers;

/**
 * @author apanasenko aka dieu
 * Date: 03.06.2009
 * Time: 16:40:57
 */
public class ParseContext {
    private Aggregator aggregator = new Aggregator();

    public Aggregator getAggregator() {
        return aggregator;
    }

    public void setAggregator(Aggregator aggregator) {
        this.aggregator = aggregator;
    }
}
