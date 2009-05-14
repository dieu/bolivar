package com.griddynamics.terracotta.parser.separate;

import commonj.work.Work;
import com.griddynamics.terracotta.util.FileUtil;

/**
 * @author agorbunov @ 08.05.2009 15:10:15
 */
public class DownloadLog implements Work {
    private String localDir;
    private Wget wget;

    public static Work fromTo(String url, String localDir) {
        return new LocalWork(DownloadLog.class, url, localDir);
    }

    /* Instead of this constructor, call DownloadLog.fromTo(url, localDir).
     * The constructor is private, but marked as public to suit Terracotta. */
    @Deprecated
    public DownloadLog(String url, String localDir) {
        this.localDir = localDir;
        wget = new Wget(url, localDir);
    }

    public void run() {
        downloadLog();
    }

    private void downloadLog() {
        createDestDir();
        startDownloading();
        waitUntilDownloadCompletes();
    }

    private void createDestDir() {
        FileUtil.createDirIfNotExists(localDir);
    }

    private void startDownloading() {
        wget.startDownloading();
    }

    private void waitUntilDownloadCompletes() {
        wget.waitUntilDownloadCompletes();
    }

    public boolean isDaemon() {
        return false;
    }

    public void release() {
        // Do nothing.
    }
}
