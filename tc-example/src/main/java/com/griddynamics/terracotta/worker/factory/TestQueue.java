package com.griddynamics.terracotta.worker.factory;

import com.griddynamics.terracotta.helpers.MyCountdownLatch;
import com.griddynamics.terracotta.helpers.TimeMetr;
import com.griddynamics.terracotta.helpers.TypeMeasurement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

/**
 * @author: apanasenko aka dieu
 * Date: 04.06.2009
 * Time: 13:16:29
 */
public class TestQueue implements Runnable{
    private Logger logger = Logger.getLogger(TestQueue.class);
    private static MyCountdownLatch cdl;
    @SuppressWarnings({"MismatchedQueryAndUpdateOfCollection"})
    private static LinkedBlockingQueue<TimeMetr> queue = new LinkedBlockingQueue<TimeMetr>();
    private static String localDir;
    @SuppressWarnings({"MismatchedQueryAndUpdateOfCollection"})
    private static final LinkedBlockingQueue<TimeMetr> timeMetrList = new LinkedBlockingQueue<TimeMetr>();
    private List<TimeMetr> timeMetrs = new ArrayList<TimeMetr>();

    public void run() {
        runDownload();
    }

    private void runDownload() {
        long timeout = Long.MAX_VALUE;
        try {
            while(true) {
                long startPeek = System.currentTimeMillis(); 
                TimeMetr task = queue.poll(timeout, TimeUnit.SECONDS);
                if(task != null) {
                    timeout = 1;
                    long startPut = task.getPutQueue();
                    long endPeek = System.currentTimeMillis();
                    cdl.countDown();
                    long endCount = System.currentTimeMillis();
                    TimeMetr timeMetr = new TimeMetr(TypeMeasurement.QUEUE);
                    timeMetr.setPutQueue(startPut);
                    timeMetr.setPeekQueue(endPeek - startPeek);
                    timeMetr.setCountDown(endCount);
                    timeMetrs.add(timeMetr);
                } else {
                    timeout = Long.MAX_VALUE;
                    logger.info("end job " + cdl.getCount());
                    for(TimeMetr timeMetr: timeMetrs) {
                        timeMetrList.put(timeMetr);
                    }
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}