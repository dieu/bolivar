package com.griddynamics.terracotta.helpers;

/**
 * @author apanasenko aka dieu
 * Date: 04.06.2009
 * Time: 13:28:27
 */
public class TaskDownloading {
    private String url;
    private String localDir;

    public TaskDownloading(String url, String localDir) {
        this.url = url;
        this.localDir = localDir;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLocalDir() {
        return localDir;
    }

    public void setLocalDir(String localDir) {
        this.localDir = localDir;
    }
}
