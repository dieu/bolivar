package com.griddynamics.terracotta.worker.factory;

import com.griddynamics.terracotta.helpers.CountdownLatch;
import com.griddynamics.terracotta.helpers.TaskDowloading;
import com.griddynamics.terracotta.helpers.Wget;
import com.griddynamics.terracotta.helpers.util.FileUtil;
import com.griddynamics.terracotta.helpers.util.NetUtil;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;

/**
 * @author: apanasenko aka dieu
 * Date: 04.06.2009
 * Time: 13:16:29
 */
public class Download implements Runnable {
    private static Logger logger = Logger.getLogger(Download.class);
    private static CountdownLatch cdl;
    private static BlockingQueue<TaskDowloading> queue = new LinkedBlockingQueue<TaskDowloading>();
    private static String localDir;

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
            TaskDowloading task = queue.take();
            logger.info("<dow>" + NetUtil.host() + "</dow>");
            logger.info(task.getUrl());
            FileUtil.createDirIfNotExists(localDir);
            Wget wget = new Wget(task.getUrl(), localDir);
            wget.startDownloading();
            wget.waitUntilDownloadCompletes();
            cdl.countDown();
        }
    }
}
