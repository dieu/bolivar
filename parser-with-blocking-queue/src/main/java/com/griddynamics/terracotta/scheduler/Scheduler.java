package com.griddynamics.terracotta.scheduler;

import com.griddynamics.terracotta.helpers.*;
import com.griddynamics.terracotta.helpers.util.FileUtil;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author: apanasenko aka dieu
 * Date: 04.06.2009
 * Time: 13:20:21
 */
public class Scheduler {
    private int numnerOfWorker;
    private long endParsing;
    private long returning;
    private String masterDir;
    private String httpUrl;
    private Aggregator aggregator;
    private static Logger logger = Logger.getLogger(Scheduler.class);
    private static String localDir;
    private static CountdownLatch cdl = new CountdownLatch();
    @SuppressWarnings({"MismatchedQueryAndUpdateOfCollection"})
    private static BlockingQueue<TaskDowloading> queue = new LinkedBlockingQueue<TaskDowloading>();
    private static final ParseContext parseContext = new ParseContext();

    public Scheduler(String numnerOfWorker, String masterDir, String httpUrl, String localDir) {
        this.numnerOfWorker = new Integer(numnerOfWorker);
        this.masterDir = masterDir;
        Scheduler.localDir = localDir;
        this.httpUrl = httpUrl;
    }

    public void start() throws InterruptedException {
        dowload();
        parsing(numnerOfWorker);
        report();
    }

    private void dowload() throws InterruptedException {
        logger.info("Downloading...");
        Long startedDownloading = System.currentTimeMillis();
        String[] logs = logs();
        cdl.reset(logs.length);
        for(String log: logs) {
            TaskDowloading task = new TaskDowloading(fileToUrl(log));
            queue.put(task);
        }
        cdl.await();
        logger.info("Downloaded in " + (System.currentTimeMillis() - startedDownloading));
    }

    private void parsing(int numberOfWorker) {
        logger.info("Parsing...");
        long startedParsing = System.currentTimeMillis();
        synchronized (parseContext) {
            cdl.reset(numberOfWorker);
            parseContext.notifyAll();
        }
        try {
            cdl.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        returning = System.currentTimeMillis();
        aggregator = parseContext.getAggregator();
        returning = System.currentTimeMillis() - returning;
        endParsing = System.currentTimeMillis() - startedParsing;
        logger.info("Parsed in " + endParsing);
    }

    private String[] logs() {
        File logs = new File(masterDir);
        FileUtil.verifyDirExists(logs);
        return logs.list();
    }

    private String fileToUrl(String file) {
        int index = file.lastIndexOf("/") < 0 ? file.lastIndexOf("\\") : file.lastIndexOf("/");
        return httpUrl + file.substring(index < 0 ? 0 : index);
    }

    private void report() {
        String ip = aggregator.ipWithMaxTraffic();
        logger.info("Ip <ip>" + ip + "</ip> has maximum traffic: <traf>" + aggregator.getTraffic() + "</traf> ");
        logger.info("Total time <to>" + endParsing + "</to>");
        logger.info("Parsing <op>" + aggregator.getAvgTimeParsing() + "</op>");
        logger.info("Returning <or>" + returning + "</or>");
    }
}
