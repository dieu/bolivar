package com.griddynamics.terracotta.helpers;

/**
 * @author apanasenko aka dieu
 * Date: 04.06.2009
 * Time: 13:28:27
 */
public class TaskDownloading {
    private String url;

    public TaskDownloading(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
