package com.griddynamics.terracotta.helpers;

import com.griddynamics.terracotta.helpers.util.FileUtil;
import com.griddynamics.terracotta.helpers.util.NetUtil;
import org.terracotta.modules.concurrent.collections.ConcurrentStringMap;
import org.apache.log4j.Logger;

import java.io.File;

/**
 * @author agorbunov @ 08.05.2009 15:11:36
 */
public class ParseLogs {
    private static Logger logger = Logger.getLogger(ParseLogs.class);
    private long startParsing;
    private ConcurrentStringMap<Long> trafficByIp = new ConcurrentStringMap<Long>();
    private Aggregator aggregator;
    private String dir;
    private File[] logs;

    public ParseLogs(String dir, Aggregator aggregator, long startParsing) {
        this.dir = dir;
        this.aggregator = aggregator;
        this.startParsing = startParsing;
    }

    public void run() {
        parseLogsInDir();
    }

    private void parseLogsInDir() {
        find();
        parse();
        report();
    }

    private void find() {
        FileUtil.verifyDirExists(dir);
        logs = new File(dir).listFiles();
    }

    private void parse() {
        logger.info("Parsing...");
        for (File log : logs)  {
            logger.info(log);
            new ParseLog(log).parseTo(trafficByIp);
        }
    }

    private void report() {
        if (!trafficByIp.isEmpty()) {
            long endParsing = System.currentTimeMillis() - startParsing;
            aggregator.add(NetUtil.worker(), trafficByIp, endParsing);
        }
    }
}
