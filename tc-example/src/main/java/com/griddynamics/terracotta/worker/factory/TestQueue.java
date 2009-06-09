package com.griddynamics.terracotta.worker.factory;

import com.griddynamics.terracotta.helpers.*;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.Logger;

/**
 * @author: apanasenko aka dieu
 * Date: 04.06.2009
 * Time: 13:16:29
 */
public class TestQueue implements Runnable {
    private static final Logger logger = Logger.getLogger(TestQueue.class);
    private static MyCountdownLatch cdl;
    private static LinkedBlockingQueue<TimeMeter> queue = new LinkedBlockingQueue<TimeMeter>();
    private static String localDir;
    @SuppressWarnings({"MismatchedQueryAndUpdateOfCollection"})
    private static final List<TimeMeter> timeMeterList = new ArrayList<TimeMeter>();

    public void run() {
        runDownload();
    }

    private void runDownload() {
        try {
            while (true) {
//
//                timeMeter.setStartMeasurement(System.currentTimeMillis());
                Long startedTaking = System.currentTimeMillis();
                TimeMeter task = queue.take();
                logger.info("Took in " + (System.currentTimeMillis() - startedTaking));
                TimeMeter timeMeter = new TimeMeter(TypeMeasurement.TAKEQUEUE);
                timeMeter.setStartMeasurement(task.getStartMeasurement());
                long end = System.currentTimeMillis();
                timeMeter.setEndMeasurement(end);
//                timeMeter.setEndMeasurement(System.currentTimeMillis());
                synchronized (timeMeterList) {
                    timeMeterList.add(timeMeter);
                }
                cdl.countDown();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}