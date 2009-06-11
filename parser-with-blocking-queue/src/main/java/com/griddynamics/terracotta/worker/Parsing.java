package com.griddynamics.terracotta.worker;

import com.griddynamics.terracotta.helpers.ParseLogs;
import static com.griddynamics.terracotta.scheduler.Scheduler.*;
import org.apache.log4j.Logger;

/**
 * @author apanasenko aka dieu
 * Date: 03.06.2009
 * Time: 15:33:19
 */
public class Parsing implements Runnable {
    private static Logger logger = Logger.getLogger(Parsing.class);

    public void run() {
        runParsing();
    }

    private void runParsing() {
        try {
            synchronized (parseContext) {
                parseContext.wait();
            }
            logger.info("Parsing...");
            ParseLogs parseLogs = new ParseLogs(workerDir, parseContext.getAggregator());
            parseLogs.run();
            cdl.countDown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
