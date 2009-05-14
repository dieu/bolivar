package com.griddynamics.equestrian.model;

/**
 * @author: apanasenko aka dieu
 * Date: 06.05.2009
 * Time: 15:55:03
 */
public class Application {
    private boolean serverStatus = true;
    private boolean workerStatus = true;
    private boolean scheluderStatus = true;
    private String applicationStatus = "";
    private String time = "";
    private String ip = "";
    private String traf = "";

    public Application() {
    }

    public boolean isServerStatus() {
        return serverStatus;
    }

    public void setServerStatus(boolean serverStatus) {
        this.serverStatus = serverStatus;
    }

    public boolean isWorkerStatus() {
        return workerStatus;
    }

    public void setWorkerStatus(boolean workerStatus) {
        this.workerStatus = workerStatus;
    }

    public boolean isScheluderStatus() {
        return scheluderStatus;
    }

    public void setScheluderStatus(boolean scheluderStatus) {
        this.scheluderStatus = scheluderStatus;
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
