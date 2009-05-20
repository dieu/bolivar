package com.griddynamics.terracotta.parser.separate_downloading;

import commonj.work.Work;
import com.griddynamics.terracotta.util.FileUtil;
import com.griddynamics.terracotta.parser.Aggregator;
import com.griddynamics.terracotta.parser.ParseLog;

import java.io.File;
import java.util.Map;
import java.util.HashMap;

import org.apache.log4j.Logger;

/**
 * @author agorbunov @ 08.05.2009 15:11:36
 */
public class ParseLogs implements Work {
    private static Logger logger = Logger.getLogger(ParseLogs.class);
    private Map<String, Long> trafficByIp = new HashMap<String, Long>();
    private Aggregator aggregator;
    private String dir;
    private File[] logs;

    public static Work inUsing(String dir, Aggregator aggregator) {
        return new LocalWork(ParseLogs.class, dir, aggregator);
    }

    /* Instead of this constructor, call ParseLogs.inUsing(dir, aggregator).
     * The constructor is private, but marked as public to suit Terracotta. */
    @Deprecated
    public ParseLogs(String dir, Aggregator aggregator) {
        this.dir = dir;
        this.aggregator = aggregator;
    }

    public void run() {
        find();
        parse();
        report();
    }

    private void find() {
        FileUtil.verifyDirExists(dir);
        logs = new File(dir).listFiles();
    }

    private void parse() {
        for (File log : logs)
            parseIfNeeded(log);
    }

    private void parseIfNeeded(File log) {
        if (isParsed(log))
            return;
        markAsParsed(log);
        parse(log);
    }

    private boolean isParsed(File log) {
        return aggregator.isParsed(log.getName());
    }

    private void markAsParsed(File log) {
        aggregator.markAsParsed(log.getName());
    }

    private void parse(File log) {
        logger.info("Parsing log " + log.getPath());
        Long started = System.currentTimeMillis();
        ParseLog work = new ParseLog(log, aggregator);
        work.parseTo(trafficByIp);
        logger.info("Parsed in " + (System.currentTimeMillis() - started));
    }

    private void report() {
        if (!trafficByIp.isEmpty()) {
            logger.info("Reporting traffuc ysage...");
            Long started = System.currentTimeMillis();
            aggregator.addStatistics(trafficByIp);
            logger.info("Reported in " + (System.currentTimeMillis() - started));
        }
    }

    public boolean isDaemon() {
        return false;
    }

    public void release() {
        // Do nothing.
    }
}
