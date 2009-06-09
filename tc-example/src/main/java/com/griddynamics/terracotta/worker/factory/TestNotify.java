package com.griddynamics.terracotta.worker.factory;

import com.griddynamics.terracotta.helpers.*;

import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.Logger;

/**
 * @author: apanasenko aka dieu
 * Date: 03.06.2009
 * Time: 15:33:19
 */
public class TestNotify implements Runnable {
    private static final Logger logger = Logger.getLogger(TestNotify.class);
    public static final ParseContext parseContext = new ParseContext();
    public static MyCountdownLatch cdl;
    public static String localDir;
    public static long startTime;
    @SuppressWarnings({"MismatchedQueryAndUpdateOfCollection"})
    private static final List<TimeMeter> timeMeterList = new ArrayList<TimeMeter>();


    public void run() {
        runParsing();
    }

    private void runParsing() {
        try {
            long endNotify;
            logger.info("Parsing");
            synchronized (parseContext) {
                logger.info("Waiting for notification");
                parseContext.wait();
                endNotify = System.currentTimeMillis();
                logger.info("Received");
            }
            TimeMeter timeMeter = new TimeMeter(TypeMeasurement.NOTIFY);
            timeMeter.setStartMeasurement(startTime);
            timeMeter.setEndMeasurement(endNotify);
            synchronized (timeMeterList) {
                timeMeterList.add(timeMeter);
            }
            logger.info("Marking work as completed");
            cdl.countDown();
            logger.info("Done");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}