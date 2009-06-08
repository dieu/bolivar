package com.griddynamics.terracotta.worker.factory;

import com.griddynamics.terracotta.helpers.MyCountdownLatch;
import com.griddynamics.terracotta.helpers.TimeMetr;
import com.griddynamics.terracotta.helpers.TypeMeasurement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author: apanasenko aka dieu
 * Date: 04.06.2009
 * Time: 13:16:29
 */
public class TestQueue implements Runnable{
    private static MyCountdownLatch cdl;
    private static LinkedBlockingQueue<TimeMetr> queue = new LinkedBlockingQueue<TimeMetr>();
    private static String localDir;
    @SuppressWarnings({"MismatchedQueryAndUpdateOfCollection"})
    private static final List<TimeMetr> timeMetrList = Collections.synchronizedList(new ArrayList<TimeMetr>());

    public void run() {
        runDownload();
    }

    private void runDownload() {
        try {
            while(true) {
//
//                timeMetr.setStartMeasurement(System.currentTimeMillis());
                TimeMetr task = queue.take();
                long end = System.currentTimeMillis();
                TimeMetr timeMetr = new TimeMetr(TypeMeasurement.TAKEQUEUE);
                timeMetr.setStartMeasurement(task.getStartMeasurement());
                timeMetr.setEndMeasurement(end);
//                timeMetr.setEndMeasurement(System.currentTimeMillis());
                synchronized (timeMetrList) {
                    timeMetrList.add(timeMetr);
                }
                cdl.countDown();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}