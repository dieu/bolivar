package com.griddynamics.terracotta.scheduler;

import com.griddynamics.terracotta.parser.separate.ParseLogs;
import commonj.work.Work;
import org.apache.log4j.Logger;

import java.util.concurrent.CountDownLatch;

/**
 * @author: apanasenko aka dieu
 * Date: 03.06.2009
 * Time: 15:33:19
 */
public class Worker implements Work{
    public static final ParseContext parseContext = new ParseContext();
    public static MyCountdownLatch cdl;
    private static Logger logger = Logger.getLogger(Worker.class);

    public boolean isDaemon() {
        return false;
    }

    public void release() {
        // Do nothing.
    }

    public void run() {
        runParsing();
    }

    private void runParsing() {
        try {
            synchronized (parseContext) {
                parseContext.wait();
            }
            logger.info("Startet Parsing " + cdl.getCount() + " " + parseContext.getWorkerDir());
            ParseLogs parseLogs = new ParseLogs(parseContext.getWorkerDir(), parseContext.getAggregator());
            parseLogs.run();
            cdl.countDown();
            logger.info("End Parsing " + cdl.getCount() + " " + parseContext.getWorkerDir());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
