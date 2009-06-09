package com.griddynamics.terracotta.worker.factory;

import com.griddynamics.terracotta.helpers.MyCountdownLatch;
import com.griddynamics.terracotta.helpers.TimeMeter;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author: apanasenko aka dieu
 * Date: 04.06.2009
 * Time: 13:16:29
 */
public class Download implements Runnable {
    private static MyCountdownLatch cdl;
    private static LinkedBlockingQueue<TimeMeter> queue = new LinkedBlockingQueue<TimeMeter>();
    private static String localDir;

    public void run() {
        runDownload();
    }

    private void runDownload() {
        //            TaskDowloading task = queue.take();
//            FileUtil.createDirIfNotExists(task.getLocalDir());
//            Wget wget = new Wget(task.getUrl(), localDir);
//            wget.startDownloading();
//            wget.waitUntilDownloadCompletes();
        cdl.countDown();
    }
}
