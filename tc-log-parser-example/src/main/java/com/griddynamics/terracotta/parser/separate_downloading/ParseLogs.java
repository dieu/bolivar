package com.griddynamics.terracotta.parser.separate_downloading;

import commonj.work.Work;
import com.griddynamics.terracotta.util.FileUtil;
import com.griddynamics.terracotta.parser.Aggregator;
import com.griddynamics.terracotta.parser.ParseLog;

import java.io.File;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import static java.lang.Math.max;

import org.apache.log4j.Logger;

/**
 * @author agorbunov @ 08.05.2009 15:11:36
 */
public class ParseLogs implements Work {

    public static class Performance {
        public Long parsed = 0L;
        public Long parsedOne = 0L;
        public Long returned = 0L;
        public Long logs = 0L;

        public static Performance average(Collection<Performance> workers) {
            Performance average = new Performance();
            Long parsedTotal = 0L;
            Long returnedTotal = 0L;
            Long logsTotal = 0L;
            Long maxLogs = 0L;
            for (Performance w : workers) {
                parsedTotal += w.parsed;
                returnedTotal += w.returned;
                logsTotal += w.logs;
                maxLogs = max(maxLogs, w.logs);
            }
            average.parsed = parsedTotal / workers.size();
            average.parsedOne = parsedTotal / logsTotal;
            average.returned = returnedTotal / workers.size();
            average.logs = maxLogs;
            return average;
        }
    }

    private static Logger logger = Logger.getLogger(ParseLogs.class);
    private Map<String, Long> trafficByIp = new HashMap<String, Long>();
    private Aggregator aggregator;
    private String dir;
    private File[] logs;
    private Performance performance = new Performance();

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
        performance.logs = (long) logs.length;
    }

    private void parse() {
        Long startedAll = System.currentTimeMillis();
        for (File log : logs)
            parseIfNeeded(log);
        performance.parsed = System.currentTimeMillis() - startedAll;
        performance.parsedOne = performance.parsed / logs.length;
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
        Long startedOne = System.currentTimeMillis();
        ParseLog parser = new ParseLog(log, aggregator);
        parser.parseTo(trafficByIp);
        logger.info("Parsed log in " + (System.currentTimeMillis() - startedOne));
    }

    private void report() {
        if (!trafficByIp.isEmpty()) {
            logger.info("Returning traffic usage...");
            Long started = System.currentTimeMillis();
            aggregator.add(trafficByIp);
            performance.returned = System.currentTimeMillis() - started;
            logger.info("Returned in " + performance.returned);
            aggregator.reportParsingPerformance(performance);
        }
    }

    public boolean isDaemon() {
        return false;
    }

    public void release() {
        // Do nothing.
    }
}
