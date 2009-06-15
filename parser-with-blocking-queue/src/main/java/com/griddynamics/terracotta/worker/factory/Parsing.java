package com.griddynamics.terracotta.worker.factory;

import com.griddynamics.terracotta.helpers.ParseLogs;
import com.griddynamics.terracotta.helpers.CountdownLatch;
import com.griddynamics.terracotta.helpers.ParseContext;
import com.griddynamics.terracotta.helpers.util.NetUtil;
import org.apache.log4j.Logger;

/**
 * @author: apanasenko aka dieu
 * Date: 03.06.2009
 * Time: 15:33:19
 */
public class Parsing implements Runnable {
    private static Logger logger = Logger.getLogger(Parsing.class);
    public static final ParseContext parseContext = new ParseContext();
    public static CountdownLatch cdl;
    public static String localDir;

    public void run() {
        runParsing();
    }

    private void runParsing() {
        try {
            synchronized (parseContext) {
                parseContext.wait();
            }
            logger.info("<par>" + NetUtil.host() + "</par>");
            ParseLogs parseLogs = new ParseLogs(localDir, parseContext.getAggregator(), System.currentTimeMillis());
            parseLogs.run();
            cdl.countDown();
            logger.info("<fin>" + NetUtil.host() + "</fin>");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
