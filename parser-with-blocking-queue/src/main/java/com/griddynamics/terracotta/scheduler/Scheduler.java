package com.griddynamics.terracotta.scheduler;

import com.griddynamics.terracotta.helpers.*;
import com.griddynamics.terracotta.helpers.util.FileUtil;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author apanasenko aka dieu
 * Date: 04.06.2009
 * Time: 13:20:21
 */
public class Scheduler {
    public static final ParseContext parseContext = new ParseContext();
    public static BlockingQueue<TaskDownloading> queue = new LinkedBlockingQueue<TaskDownloading>();
    public static CountdownLatch cdl = new CountdownLatch();
    public static String workerDir;
    private static Logger logger = Logger.getLogger(Scheduler.class);
    private String masterDir;
    private String masterUrl;
    private int workerCount;
    private Aggregator aggregator;

    public Scheduler(String workerCount, String masterDir, String masterUrl, String workerDir) {
        this.workerCount = new Integer(workerCount);
        this.masterDir = masterDir;
        Scheduler.workerDir = workerDir;
        this.masterUrl = masterUrl;
    }

    public void run() throws InterruptedException {
        dowload();
        parse();
        report();
    }

    private void dowload() throws InterruptedException {
        logger.info("Downloading...");
        Long startedDownloading = System.currentTimeMillis();
        String[] logs = logs();
        cdl.reset(logs.length);
        for(String log: logs) {
            TaskDownloading task = new TaskDownloading(fileToUrl(log), workerDir);
            queue.put(task);
        }
        cdl.await();
        logger.info("Downloaded in " + (System.currentTimeMillis() - startedDownloading));
    }

    private void parse() {
        logger.info("Parsing...");
        Long startedParsing = System.currentTimeMillis();
        synchronized (parseContext) {
            cdl.reset(workerCount);
            parseContext.notifyAll();
        }
        try {
            cdl.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        aggregator = parseContext.getAggregator();
        logger.info("Parsed in " + (System.currentTimeMillis() - startedParsing));
    }

    private String[] logs() {
        File logs = new File(masterDir);
        FileUtil.verifyDirExists(logs);
        return logs.list();
    }

    private String fileToUrl(String file) {
        int index = file.lastIndexOf("/") < 0 ? file.lastIndexOf("\\") : file.lastIndexOf("/");
        return masterUrl + file.substring(index < 0 ? 0 : index);
    }

    private void report() {
        String ip = aggregator.ipWithMaxTraffic();
        logger.info("Ip <ip>" + ip + "</ip> has maximum traffic: <traf>" + aggregator.getTraffic() + "</traf> ");
    }
}
