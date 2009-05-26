package com.griddynamics.terracotta.parser.separate_downloading;

import commonj.work.Work;
import com.griddynamics.terracotta.util.FileUtil;
import static com.griddynamics.terracotta.parser.separate_downloading.UiTracker.Phase.DOWNLOADING;

/**
 * @author agorbunov @ 08.05.2009 15:10:15
 */
public class DownloadLog implements Work {
    private UiTracker tracker = new UiTracker();
    private Wget wget;
    private String dir;

    public static Work fromTo(String url, String dir) {
        return new LocalWork(DownloadLog.class, url, dir);
    }

    /* Instead of this constructor, call DownloadLog.fromTo(url, dest).
     * The constructor is private, but marked as public to suit Terracotta. */
    @Deprecated
    public DownloadLog(String url, String dir) {
        wget = new Wget(url, dir);
        this.dir = dir;
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
        FileUtil.createDirIfNotExists(dir);
    }

    private void startDownloading() {
        tracker.entered(DOWNLOADING);
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
