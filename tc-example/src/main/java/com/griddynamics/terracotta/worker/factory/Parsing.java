package com.griddynamics.terracotta.worker.factory;

import com.griddynamics.terracotta.helpers.ParseLogs;
import com.griddynamics.terracotta.helpers.MyCountdownLatch;
import com.griddynamics.terracotta.helpers.ParseContext;

/**
 * @author: apanasenko aka dieu
 * Date: 03.06.2009
 * Time: 15:33:19
 */
public class Parsing implements Runnable {
    public static final ParseContext parseContext = new ParseContext();
    public static MyCountdownLatch cdl;
    public static String localDir;

    public void run() {
        runParsing();
    }

    private void runParsing() {
        try {
            synchronized (parseContext) {
                parseContext.wait();
            }
            ParseLogs parseLogs = new ParseLogs(localDir, parseContext.getAggregator());
            parseLogs.run();
            cdl.countDown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
