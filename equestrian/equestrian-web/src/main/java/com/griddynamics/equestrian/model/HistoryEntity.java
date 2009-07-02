package com.griddynamics.equestrian.model;

import java.util.List;
import java.util.ArrayList;

/**
 * @author: apanasenko aka dieu
 * Date: 18.05.2009
 * Time: 16:00:30
 */
public class HistoryEntity {                                     
    private List<String> date = new ArrayList<String>();
    private List<String> parsing = new ArrayList<String>();
    private List<String> returning = new ArrayList<String>();
    private List<String> time = new ArrayList<String>();
    private List<String> traf = new ArrayList<String>();
    private List<String> ip = new ArrayList<String>();
    private List<String> workers = new ArrayList<String>();

    public HistoryEntity() {
    }

    public List<String> getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date.add(date);
    }
            
    public List<String> getParsing() {
        return parsing;
    }

    public void setParsing(String parsing) {
        this.parsing.add(parsing);
    }

    public List<String> getReturning() {
        return returning;
    }

    public void setReturning(String returning) {
        this.returning.add(returning);
    }

    public List<String> getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time.add(time);
    }

    public List<String> getTraf() {
        return traf;
    }

    public void setTraf(String traf) {
        this.traf.add(traf);
    }

    public List<String> getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip.add(ip);
    }

    public List<String> getWorkers() {
        return workers;
    }

    public void setWorkers(String workers) {
        this.workers.add(workers);
    }
}

