package com.griddynamics.terracotta.worker.factory;

import com.griddynamics.terracotta.helpers.TaskDownloading;
import com.griddynamics.terracotta.helpers.Wget;
import com.griddynamics.terracotta.helpers.Pipe;
import com.griddynamics.terracotta.helpers.util.FileUtil;
import com.griddynamics.terracotta.helpers.util.NetUtil;
import static com.griddynamics.terracotta.scheduler.Scheduler.*;
import org.apache.log4j.Logger;
import org.terracotta.modules.concurrent.collections.ConcurrentStringMap;

import java.util.HashMap;

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
        do {
            TaskDownloading task = queue.take();
            logger.info("<dow>" + NetUtil.host() + "</dow>");
            logger.info(task.getUrl());
            FileUtil.createDirIfNotExists(workerDir);
            Wget wget = new Wget(task.getUrl(), workerDir);
            wget.startDownloading();
            wget.waitUntilDownloadCompletes();            
            cdl.countDown();
        } while (cdl.getCount() > 0);
    }
}
