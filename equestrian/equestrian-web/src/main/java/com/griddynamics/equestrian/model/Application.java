package com.griddynamics.equestrian.model;

import java.util.*;

/**
 * @author: apanasenko aka dieu
 * Date: 06.05.2009
 * Time: 15:55:03
 */
public class Application {
    private boolean schedulerStatus = true;
    private Date date = null;
    private String workers = "";
    private String parsing = "";
    private String returning = "";
    private String time = "";
    private String ip = "";
    private String traf = "";
    private String nodeIp = "";

    public Application() {
    }  

    public boolean isSchedulerStatus() {
        return schedulerStatus;
    }

    public void setSchedulerStatus(boolean schedulerStatus) {
        this.schedulerStatus = schedulerStatus;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getWorkers() {
        return workers;
    }

    public void setWorkers(String workers) {
        this.workers = workers;
    }

    public String getParsing() {
        return parsing;
    }

    public void setParsing(String parsing) {
        this.parsing = parsing;
    }

    public String getReturning() {
        return returning;
    }

    public void setReturning(String returning) {
        this.returning = returning;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getTraf() {
        return traf;
    }

    public void setTraf(String traf) {
        this.traf = traf;
    }

    public String getNodeIp() {
        return nodeIp;
    }

    public void setNodeIp(String nodeIp) {
        this.nodeIp = nodeIp;
    }
}
