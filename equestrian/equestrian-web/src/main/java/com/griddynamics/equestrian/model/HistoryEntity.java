package com.griddynamics.equestrian.model;

import java.util.List;
import java.util.ArrayList;

/**
 * @author: apanasenko aka dieu
 * Date: 18.05.2009
 * Time: 16:00:30
 */
public class HistoryEntity {                                     
    private List date = new ArrayList();
    private List removing = new ArrayList();
    private List dowloading = new ArrayList();
    private List parsing = new ArrayList();
    private List aggregating = new ArrayList();
    private List time = new ArrayList();
    private List traf = new ArrayList();
    private List ip = new ArrayList();
    private List workers = new ArrayList();

    public HistoryEntity() {
    }

    public List getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date.add(date);
    }

    public List getRemoving() {
        return removing;
    }

    public void setRemoving(String removing) {
        this.removing.add(removing);
    }

    public List getDowloading() {
        return dowloading;
    }

    public void setDowloading(String dowloading) {
        this.dowloading.add(dowloading);
    }

    public List getParsing() {
        return parsing;
    }

    public void setParsing(String parsing) {
        this.parsing.add(parsing);
    }

    public List getAggregating() {
        return aggregating;
    }

    public void setAggregating(String aggregating) {
        this.aggregating.add(aggregating);
    }

    public List getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time.add(time);
    }

    public List getTraf() {
        return traf;
    }

    public void setTraf(String traf) {
        this.traf.add(traf);
    }

    public List getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip.add(ip);
    }

    public List getWorkers() {
        return workers;
    }

    public void setWorkers(String workers) {
        this.workers.add(workers);
    }
}
