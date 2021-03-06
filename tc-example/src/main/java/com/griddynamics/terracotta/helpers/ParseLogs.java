package com.griddynamics.terracotta.helpers;

import com.griddynamics.terracotta.helpers.util.FileUtil;
import com.griddynamics.terracotta.helpers.util.NetUtil;
import org.terracotta.modules.concurrent.collections.ConcurrentStringMap;

import java.io.File;

/**
 * @author agorbunov @ 08.05.2009 15:11:36
 */
public class ParseLogs {
    private ConcurrentStringMap<Long> trafficByIp = new ConcurrentStringMap<Long>();
    private Aggregator aggregator;
    private String dir;
    private File[] logs;

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
    }

    private void parse() {
        for (File log : logs)  {
            trafficByIp.putAll(new ParseLog(log).parseTo());
        }
    }

    private void report() {
        if (!trafficByIp.isEmpty()) {
            aggregator.add(NetUtil.worker(), trafficByIp);
        }
    }
}
