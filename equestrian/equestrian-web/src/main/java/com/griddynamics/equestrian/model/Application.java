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
    private String applicationStatus = "";
    private String time = "";
    private String ip = "";
    private String traf = "";
    private Map<String, Boolean> nodeIp = new HashMap<String, Boolean>();

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

    public String getApplicationStatus() {
        return applicationStatus;
    }

    public void setApplicationStatus(String applicationStatus) {
        this.applicationStatus = applicationStatus;
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

    public Map<String, Boolean> getNodeIp() {
        return nodeIp;
    }

    public void setNodeIp(Map<String, Boolean> nodeIp) {
        this.nodeIp = nodeIp;
    }
}
