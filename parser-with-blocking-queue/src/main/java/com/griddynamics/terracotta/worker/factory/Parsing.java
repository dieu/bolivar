package com.griddynamics.terracotta.worker.factory;

import com.griddynamics.terracotta.helpers.ParseLogs;
import com.griddynamics.terracotta.helpers.util.NetUtil;
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
            logger.info("<par>" + NetUtil.host() + "</par>");
            logger.info("Parsing...");
            ParseLogs parseLogs = new ParseLogs(workerDir, parseContext.getAggregator(), System.currentTimeMillis());
            parseLogs.run();
            cdl.countDown();
            logger.info("<fin>" + NetUtil.host() + "</fin>");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
