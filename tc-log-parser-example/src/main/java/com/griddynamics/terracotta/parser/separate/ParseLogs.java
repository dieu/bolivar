package com.griddynamics.terracotta.parser.separate;

import commonj.work.Work;
import com.griddynamics.terracotta.util.FileUtil;
import com.griddynamics.terracotta.parser.Aggregator;
import com.griddynamics.terracotta.parser.combined.ParseLog;
import com.griddynamics.terracotta.parser.separate.Tracker;
import com.griddynamics.terracotta.parser.separate.Trackable;

import java.io.File;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import static java.lang.Math.max;

import org.apache.log4j.Logger;

import com.griddynamics.terracotta.parser.separate.Tracker.Phase;

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
            Long maxLogsPerWorker = 0L;
            for (Performance w : workers) {
                parsedTotal += w.parsed;
                returnedTotal += w.returned;
                logsTotal += w.logs;
                maxLogsPerWorker = max(maxLogsPerWorker, w.logs);
            }
            average.parsed = parsedTotal / workers.size();
            average.parsedOne = parsedTotal / logsTotal;
            average.returned = returnedTotal / workers.size();
            average.logs = maxLogsPerWorker;
            return average;
        }
    }

    private static Logger logger = Logger.getLogger(ParseLogs.class);
    private Tracker tracker = new Tracker();
    private Map<String, Long> trafficByIp = new HashMap<String, Long>();
    private Aggregator aggregator;
    private String dir;
    private File[] logs;
    private int logCount;
    private Performance performance = new Performance();

    public static Work inUsing(String dir, Aggregator aggregator) {
        return new Trackable(ParseLogs.class, dir, aggregator);
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
        logCount = logs.length;
        performance.logs = (long) logCount;
    }

    private void parse() {
        // TODO Move measurements to tracker
        Long started = System.currentTimeMillis();
        parseLogs();
        performance.parsed = System.currentTimeMillis() - started;
        performance.parsedOne = performance.parsed / logCount;
    }

    private void parseLogs() {
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
        // TODO Move measurements to tracker
        logger.info("Parsing log " + log.getPath());
        Long started = System.currentTimeMillis();
        tracker.entered(Phase.PARSING);
        ParseLog work = new ParseLog(log, aggregator);
        work.parseTo(trafficByIp);
        logger.info("Parsed log in " + (System.currentTimeMillis() - started));
    }

    private void report() {
        if (parsedSomeLogs()) {
            // TODO Move measurements to tracker
            logger.info("Returning traffic usage...");
            Long started = System.currentTimeMillis();
            tracker.entered(Phase.RETURNING);
            returnResult();
            performance.returned = System.currentTimeMillis() - started;
            logger.info("Returned in " + performance.returned);
            aggregator.reportParsingPerformance(performance);
            tracker.entered(Phase.DONE);
        }
    }

    private boolean parsedSomeLogs() {
        return !trafficByIp.isEmpty();
    }

    private void returnResult() {
        aggregator.add(trafficByIp);
    }

    public boolean isDaemon() {
        return false;
    }

    public void release() {
        // Do nothing.
    }
}
