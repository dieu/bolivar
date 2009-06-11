package com.griddynamics.terracotta.worker;

import com.griddynamics.terracotta.helpers.TaskDownloading;
import com.griddynamics.terracotta.helpers.Wget;
import com.griddynamics.terracotta.helpers.util.FileUtil;
import static com.griddynamics.terracotta.scheduler.Scheduler.cdl;
import static com.griddynamics.terracotta.scheduler.Scheduler.queue;
import org.apache.log4j.Logger;

/**
 * @author apanasenko aka dieu
 * Date: 04.06.2009
 * Time: 13:16:29
 */
public class Download implements Runnable {
    private static Logger logger = Logger.getLogger(Download.class);

    public void run() {
        try {
            runDownload();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void runDownload() throws InterruptedException {
        logger.info("Downloading...");
        while (true) {
            TaskDownloading task = queue.take();
            logger.info(task.getUrl());
            FileUtil.createDirIfNotExists(task.getLocalDir());
            Wget wget = new Wget(task.getUrl(), task.getLocalDir());
            wget.startDownloading();
            wget.waitUntilDownloadCompletes();
            cdl.countDown();
        }
    }
}
