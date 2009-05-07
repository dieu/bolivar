package com.griddynamics.terracotta;

import org.terracotta.workmanager.dynamic.DynamicWorkManager;
import org.terracotta.message.routing.RoundRobinRouter;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.List;
import java.util.ArrayList;

import com.griddynamics.terracotta.parser.Aggregator;
import com.griddynamics.terracotta.parser.ParserLogWork;
import com.griddynamics.terracotta.util.ThreadUtil;
import commonj.work.WorkItem;

/**
 * @author agorbunov @ 07.05.2009 18:12:31
 */
public class Scheduler {
    private static final Long SECOND = 1000L;
    private static Logger logger = Logger.getLogger(Scheduler.class);
    private String dir;
    private String dirUrl;
    private DynamicWorkManager workManager;
    private String[] logs;
    private List<WorkItem> workItems;
    private Long startTime;
    private Aggregator aggregator;

    public Scheduler(String dir, String dirUrl) {
        this.dir = dir;
        this.dirUrl = dirUrl;
    }

    public void launchJob() {
        connectToWorkers();
        findTrafficOfEachIp();
        findIpWithMaxTraffic();
    }

    private void connectToWorkers() {
        requestConnection();
        waitUntilAllWorkersConnected();
    }

    private void requestConnection() {
        workManager = new DynamicWorkManager(JobService.topologyName, null, new RoundRobinRouter());
    }

    private void waitUntilAllWorkersConnected() {
        logger.info("Waiting for workers...");
        ThreadUtil.sleep(30 * SECOND);
    }

    private void findTrafficOfEachIp() {
        findLogs();
        scheduleLogsForParsing();
        waitUntilAllLogsParsed();
    }

    private void findLogs() {
        File file = new File(dir);
        verifyDirectoryExists(file);
        logs = file.list();
    }

    private void verifyDirectoryExists(File file) {
        if (!file.exists())
            throw new RuntimeException("Directory " + file.getPath() + " not exists!");
        if (!file.isDirectory())
            throw new RuntimeException(file.getPath() + " is not directory!");
    }

    private void scheduleLogsForParsing() {
        startTime = System.currentTimeMillis();
        aggregator = new Aggregator();
        logger.info("Generating work items list...");
        workItems = new ArrayList<WorkItem>();
        for (String log : logs) {
            logger.info("Sheduled work for " + log);
            workItems.add(scheduleLogForParsing(log));
        }
    }

    private WorkItem scheduleLogForParsing(String log) {
        String logUrl = dirUrl + log;
        ParserLogWork work = new ParserLogWork(logUrl, aggregator);
        return workManager.schedule(work);
    }

    private void waitUntilAllLogsParsed() {
        try {
            workManager.waitForAll(workItems, Long.MAX_VALUE);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void findIpWithMaxTraffic() {
        String ip = aggregator.getIpWithMaxTraffic();
        logger.info("Work finished in <nodeTime>" + (System.currentTimeMillis() - startTime) + "</nodeTime> ");
        logger.info("Ip <ip>" + ip + "</ip> has maximum traffic: <traf>" + aggregator.getUserStat(ip) + "</traf> ");
    }
}
