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
}
