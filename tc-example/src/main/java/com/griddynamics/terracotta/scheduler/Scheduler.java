package com.griddynamics.terracotta.scheduler;

import com.griddynamics.terracotta.helpers.*;
import com.griddynamics.terracotta.helpers.util.FileUtil;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
    private static LinkedBlockingQueue<TimeMetr> queue = new LinkedBlockingQueue<TimeMetr>();
    @SuppressWarnings({"MismatchedQueryAndUpdateOfCollection"})
    private static final List<TimeMetr> timeMetrList = Collections.synchronizedList(new ArrayList<TimeMetr>());
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
        String[] logs = logs();
        cdl = new MyCountdownLatch(logs.length);
        for(String log: logs) {
            TimeMetr timeMetr = new TimeMetr(TypeMeasurement.PUTQUEUE);
            timeMetr.setStartMeasurement(System.currentTimeMillis());
            queue.put(timeMetr);
//            queue.put(new TaskDowloading(fileToUrl(log), localDir));
//            timeMetr.setEndMeasurement(System.currentTimeMillis());
//            synchronized (timeMetrList) {
//                timeMetrList.add(timeMetr);
//            }
        }
        cdl.await();
    }

    private void parsing(int numberOfWorker) {
        synchronized (parseContext) {
            startTime = System.currentTimeMillis();
            cdl = new MyCountdownLatch(numberOfWorker);
            parseContext.notifyAll();
        }
        try {
            cdl.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        aggregator = parseContext.getAggregator();
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
        synchronized (timeMetrList) {
            for(TimeMetr timeMetr: timeMetrList) {
                logger.info("R: " + timeMetr.getResultMeasurement()
                        + " T: "
                        + timeMetr.getTypeMeasurement());
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
