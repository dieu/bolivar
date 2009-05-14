package com.griddynamics.terracotta.parser.separate;

import commonj.work.Work;
import com.griddynamics.terracotta.util.FileUtil;
import com.griddynamics.terracotta.parser.Aggregator;
import com.griddynamics.terracotta.parser.ParseLog;

import java.io.File;

import org.apache.log4j.Logger;

/**
 * @author agorbunov @ 08.05.2009 15:11:36
 */
public class ParseLogs implements Work {
    private static Logger logger = Logger.getLogger(ParseLogs.class);
    private Aggregator aggregator;
    private String localDir;
    private File[] logs;

    public static Work inUsing(String localDir, Aggregator aggregator) {
        return new LocalWork(ParseLogs.class, localDir, aggregator);
    }

    /* Instead of this constructor, call ParseLogs.inUsing(localDir, aggregator). 
     * The constructor is private, but marked as public to suit Terracotta. */
    @Deprecated
    public ParseLogs(String localDir, Aggregator aggregator) {
        this.localDir = localDir;
        this.aggregator = aggregator;
    }

    public void run() {
        parseDownloadedLogs();
    }

    private void parseDownloadedLogs() {
        findLogs();
        parseLogs();
    }

    private void findLogs() {
        FileUtil.verifyDirExists(localDir);
        logs = new File(localDir).listFiles();
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
        logger.info("Parsing log " + log.getPath());
        new ParseLog(log, aggregator).run();
    }

    public boolean isDaemon() {
        return false;
    }

    public void release() {
        // Do nothing.
    }
}
