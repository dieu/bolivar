package com.griddynamics.terracotta.worker.factory;

import com.griddynamics.terracotta.helpers.MyCountdownLatch;
import com.griddynamics.terracotta.helpers.ParseContext;
import com.griddynamics.terracotta.helpers.TimeMetr;
import com.griddynamics.terracotta.helpers.TypeMeasurement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author: apanasenko aka dieu
 * Date: 03.06.2009
 * Time: 15:33:19
 */
public class TestNotify implements Runnable{
    public static final ParseContext parseContext = new ParseContext();
    public static MyCountdownLatch cdl;
    public static String localDir;
    public static long startTime;
    @SuppressWarnings({"MismatchedQueryAndUpdateOfCollection"})
    private static final LinkedBlockingQueue<TimeMetr> timeMetrList = new LinkedBlockingQueue<TimeMetr>();


    public void run() {
        runParsing();
    }

    private void runParsing() {
        try {
            long endNotify;
            synchronized (parseContext) {
                parseContext.wait();
                endNotify = System.currentTimeMillis();
            }
            TimeMetr timeMetr = new TimeMetr(TypeMeasurement.NOTIFY);
            timeMetr.setPutQueue(startTime);
            timeMetr.setPeekQueue(endNotify);
            synchronized (timeMetrList) {
                timeMetrList.add(timeMetr);
            }
            cdl.countDown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}