package com.griddynamics.terracotta.scheduler;

import com.griddynamics.terracotta.helpers.*;
import com.griddynamics.terracotta.helpers.util.FileUtil;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author: apanasenko aka dieu
 * Date: 04.06.2009
 * Time: 13:20:21
 */
public class Scheduler {
    private int numnerOfWorker;
    private String masterDir;
    private String httpUrl;
    private Aggregator aggregator;
    private static Logger logger = Logger.getLogger(Scheduler.class);
    private static String localDir;
    private static long startTime;
    private static MyCountdownLatch cdl;    
    @SuppressWarnings({"MismatchedQueryAndUpdateOfCollection"})
    private static LinkedBlockingQueue<TimeMeter> queue = new LinkedBlockingQueue<TimeMeter>();
    @SuppressWarnings({"MismatchedQueryAndUpdateOfCollection"})
    private static final List<TimeMeter> timeMeterList = Collections.synchronizedList(new ArrayList<TimeMeter>());
    private static final ParseContext parseContext = new ParseContext();

    public Scheduler(String numnerOfWorker, String masterDir, String localDir, String httpUrl) {
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
        logger.info("Downloading");
        Long startedDownloading = System.currentTimeMillis();
        String[] logs = logs();
        cdl = new MyCountdownLatch(logs.length);
        for(String log: logs) {
            TimeMeter timeMeter = new TimeMeter(TypeMeasurement.PUTQUEUE);
            timeMeter.setStartMeasurement(System.currentTimeMillis());
            Long startedPutting = System.currentTimeMillis();
            queue.put(timeMeter);
            logger.info("Put in " + (System.currentTimeMillis() - startedPutting));
//            queue.put(new TaskDowloading(fileToUrl(log), localDir));
//            timeMeter.setEndMeasurement(System.currentTimeMillis());
//            synchronized (timeMeterList) {
//                timeMeterList.add(timeMeter);
//            }
        }
        cdl.await();
        logger.info("Downloaded in " + (System.currentTimeMillis() - startedDownloading));
    }

    private void parsing(int numberOfWorker) {
        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        logger.info("Parsing");
        Long startedParsing = System.currentTimeMillis();
        synchronized (parseContext) {
            logger.info("Entered parsing critical section");
            startTime = System.currentTimeMillis();
            cdl = new MyCountdownLatch(numberOfWorker);
            logger.info("Notifiying");
            parseContext.notifyAll();
            logger.info("Done");
        }
        try {
            logger.info("Waiting for workers");
            cdl.await();
            logger.info("Done");
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
        return httpUrl + file.substring(index < 0 ? 0 : index);
    }

    private void report() {
        String ip = aggregator.ipWithMaxTraffic();
        logger.info("Ip <ip>" + ip + "</ip> has maximum traffic: <traf>" + aggregator.getTraffic() + "</traf> ");
        synchronized (timeMeterList) {
            for(TimeMeter timeMeter : timeMeterList) {
                logger.info("R: " + timeMeter.getResultMeasurement()
                        + " T: "
                        + timeMeter.getTypeMeasurement());
            }
        }
//        AveragePerformance ap = aggregator.averagePerformance();
//        logger.info("Parsing performance:");
//        logger.info("Parsed: " + encloseWithTag(ap.parsed(), "op"));
//        logger.info("Parsed min: " + ap.parsedMin());
//        logger.info("Parsed max: " + ap.parsedMax());
//        logger.info("Parsed one: " + ap.parsedOne());
//        logger.info("Returned: " + encloseWithTag(ap.returned(), "or"));
//        logger.info("Returned min: " + ap.returnedMin());
//        logger.info("Returned max: " + ap.returnedMax());
//        logger.info("Logs: " + ap.logs());
    }
}
