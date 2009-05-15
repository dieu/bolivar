package com.griddynamics.equestrian.model;

/**
 * @author: apanasenko aka dieu
 * Date: 06.05.2009
 * Time: 15:55:03
 */
public class Application {
    private boolean scheluderStatus = true;
    private int nWorkers = 0;
    private String applicationStatus = "";
    private String time = "";
    private String ip = "";
    private String traf = "";

    public Application() {
    }  

    public boolean isScheluderStatus() {
        return scheluderStatus;
    }

    public void setScheluderStatus(boolean scheluderStatus) {
        this.scheluderStatus = scheluderStatus;
    }

    public int getNWorkers() {
        return nWorkers;
    }

    public void setNWorkers(int nWorkers) {
        this.nWorkers = nWorkers;
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
}
