package com.griddynamics.terracotta.parser.separate;

import com.griddynamics.terracotta.parser.Aggregator;
import com.griddynamics.terracotta.parser.combined.ParseLog;
import static com.griddynamics.terracotta.parser.separate.Tracker.Phase.*;
import com.griddynamics.terracotta.util.FileUtil;
import commonj.work.Work;
import org.apache.log4j.Logger;

import java.io.File;
import static java.lang.Math.max;
import static java.lang.Math.min;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author agorbunov @ 08.05.2009 15:11:36
 */
public class ParseLogs implements Work {

    public static class Performance {
        public Long parsed = 0L;
        public Long parsedOne = 0L;
        public Long returned = 0L;
        public Long logs = 0L;
    }

    public static class AveragePerformance {
        private Collection<Performance> workers;
        private Long parsed;
        private Long parsedOne;
        private Long parsedMin = Long.MAX_VALUE;
        private Long parsedMax = Long.MIN_VALUE;
        private Long returned;
        private Long returnedMin = Long.MAX_VALUE;
        private Long returnedMax = Long.MIN_VALUE;
        private Long maxLogs = Long.MIN_VALUE;

        public AveragePerformance(Collection<Performance> workers) {
            this.workers = workers;
            findAverage();
        }

        private void findAverage() {
            Long parsedTotal = 0L;
            Long returnedTotal = 0L;
            Long logsTotal = 0L;
            for (Performance w : workers) {
                parsedTotal += w.parsed;
                parsedMin = min(parsedMin, w.parsed);
                parsedMax = max(parsedMax, w.parsed);
                returnedTotal += w.returned;
                returnedMin = min(returnedMin, w.returned);
                returnedMax = max(returnedMax, w.returned);
                logsTotal += w.logs;
                maxLogs = max(maxLogs, w.logs);
            }
            parsed = parsedTotal / workers.size();
            parsedOne = parsedTotal / logsTotal;
            returned = returnedTotal / workers.size();
        }

        public Long parsed() {
            return parsed;
        }

        public Long parsedMin() {
            return parsedMin;
        }

        public Long parsedMax() {
            return parsedMax;
        }

        public Long parsedOne() {
            return parsedOne;
        }

        public Long returned() {
            return returned;
        }

        public Long returnedMin() {
            return returnedMin;
        }

        public Long returnedMax() {
            return returnedMax;
        }

        public Long logs() {
            return maxLogs;
        }
    }

    private static Logger logger = Logger.getLogger(ParseLogs.class);
    private Map<String, Long> trafficByIp = new HashMap<String, Long>();
    private Performance performance = new Performance();
    private Tracker tracker = new Tracker();
    private Aggregator aggregator;
    private String dir;
    private File[] logs;

    public static Work fromTo(String dir, Aggregator aggregator) {
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
        performance.logs = (long) logs.length;
    }

    private void parse() {
        // TODO Move measurements to the tracker
        Long started = System.currentTimeMillis();
        parseLogs();
        performance.parsed = System.currentTimeMillis() - started;
        performance.parsedOne = performance.parsed / logs.length;
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
        // TODO Move measurements to the tracker
        logger.info("Parsing log " + log.getPath());
        Long started = System.currentTimeMillis();
        tracker.phase(PARSING);
        new ParseLog(log, aggregator).parseTo(trafficByIp);
        logger.info("Parsed log in " + (System.currentTimeMillis() - started));
    }

    private void report() {
        if (parsedNewLogs()) {
            reportTraffic();
            reportPerformance();
        }
    }

    private boolean parsedNewLogs() {
        return !trafficByIp.isEmpty();
    }

    private void reportTraffic() {
        // TODO Move measurements to the tracker
        logger.info("Returning parsing result...");
        Long started = System.currentTimeMillis();
        tracker.phase(RETURNING);
        aggregator.add(trafficByIp);
        performance.returned = System.currentTimeMillis() - started;
        logger.info("Returned in " + performance.returned);
    }

    private void reportPerformance() {
        Long started = System.currentTimeMillis();
        aggregator.reportPerformance(performance);
        logger.info("Reported performance in " + (System.currentTimeMillis() - started));
        tracker.phase(DONE);
    }

    public boolean isDaemon() {
        return false;
    }

    public void release() {
        // Do nothing.
    }
}
