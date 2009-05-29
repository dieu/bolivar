package com.griddynamics.terracotta.parser.separate;

import commonj.work.Work;
import com.griddynamics.terracotta.util.FileUtil;
import static com.griddynamics.terracotta.parser.separate.Tracker.Phase.DOWNLOADING;

/**
 * @author agorbunov @ 08.05.2009 15:10:15
 */
public class DownloadLog implements Work {
    private Tracker phase = new Tracker();
    private Wget wget;
    private String dir;

    public static Work fromTo(String url, String dir) {
        return new Trackable(DownloadLog.class, url, dir);
    }

    /* Instead of this constructor, call DownloadLog.fromTo(url, dest).
     * The constructor is private, but marked as public to suit Terracotta. */
    @Deprecated
    public DownloadLog(String url, String dir) {
        url = testReportingOfMissingLog(url);
        wget = new Wget(url, dir);
        this.dir = dir;
    }

    private String testReportingOfMissingLog(String url) {
        String missingLog = "traf-log-1522409746";
        if (url.contains(missingLog))
            return makeNotFound(url);
        return url;
    }

    private String makeNotFound(String url) {
        return url + "_not_found";
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
        phase.phase(DOWNLOADING);
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
