package com.griddynamics.terracotta;

import org.terracotta.workmanager.dynamic.DynamicWorkManager;
import org.terracotta.message.routing.RoundRobinRouter;
import org.apache.log4j.Logger;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.List;
import java.util.ArrayList;

import com.griddynamics.terracotta.parser.Aggregator;
import com.griddynamics.terracotta.parser.separate.RemoveLogs;
import com.griddynamics.terracotta.parser.separate.DownloadLog;
import com.griddynamics.terracotta.parser.separate.ParseLogs;
import com.griddynamics.terracotta.util.ThreadUtil;
import com.griddynamics.terracotta.util.FileUtil;
import commonj.work.WorkItem;
import commonj.work.Work;

/**
 * @author agorbunov @ 07.05.2009 18:12:31
 */
public class Scheduler {
    private static final Long SECOND = 1000L;
    private static Logger logger = Logger.getLogger(Scheduler.class);
    private String dir;
    private String dirUrl;
    private String localDir;
    private DynamicWorkManager workManager;
    private List<WorkItem> workItems;
    private Long startedDownloading;
    private Long startedParsing;
    private Aggregator aggregator;

    public Scheduler(String dir, String dirUrl, String localDir) {
        this.dir = dir;
        this.dirUrl = dirUrl;
        this.localDir = localDir;
        assertNotNull(dir);
        assertNotNull(dirUrl);
        assertNotNull(localDir);
    }

    public void findMaxTrafficWithSeveralWorkers() {
        connectToWorkers();
        findTrafficOfEachIp();
        findMaxTraffic();
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
        removeLogs();
        downloadLogs();
        parseLogs();
    }

    private void removeLogs() {
        scheduleRemoving();
        waitForWorkers();
    }

    private void scheduleRemoving() {
        logger.info("Removing logs...");
        workItems = new ArrayList<WorkItem>();
        // TODO Schedule the task according to the number of workers, rather than the number of logs
        for (String log : serverLogs())
            workItems.add(scheduleRemovingOne());
    }

    private String[] serverLogs() {
        File serverLogs = new File(dir);
        FileUtil.verifyDirExists(serverLogs);
        return serverLogs.list();
    }

    private WorkItem scheduleRemovingOne() {
        Work work = RemoveLogs.from(localDir);
        return workManager.schedule(work);
    }

    private void waitForWorkers() {
        try {
            workManager.waitForAll(workItems, Long.MAX_VALUE);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void downloadLogs() {
        scheduleDownloading();
        waitForWorkers();
        reportDownloadTime();
    }

    private void scheduleDownloading() {
        logger.info("Downloading logs...");
        startedDownloading = System.currentTimeMillis();
        workItems = new ArrayList<WorkItem>();
        for (String log : serverLogs())
            workItems.add(scheduleDownloadingOne(log));
    }

    private WorkItem scheduleDownloadingOne(String log) {
        String url = dirUrl + log;
        Work work = DownloadLog.fromTo(url, localDir);
        return workManager.schedule(work);
    }

    private void reportDownloadTime() {
        logger.info("Downloaded logs in " + (System.currentTimeMillis() - startedDownloading));
    }

    private void parseLogs() {
        scheduleParsing();
        waitForWorkers();
    }

    private void scheduleParsing() {
        logger.info("Parsing logs...");
        startedParsing = System.currentTimeMillis();
        aggregator = new Aggregator();
        workItems = new ArrayList<WorkItem>();
        // TODO Schedule the task according to the number of workers, rather than the number of logs
        for (String log : serverLogs())
            workItems.add(scheduleParsingOne());
    }

    private WorkItem scheduleParsingOne() {
        Work work = ParseLogs.inUsing(localDir, aggregator);
        return workManager.schedule(work);
    }

    private void findMaxTraffic() {
        String ip = aggregator.getIpWithMaxTraffic();
        logger.info("Parsed logs in <nodeTime>" + (System.currentTimeMillis() - startedParsing) + "</nodeTime> ");
        logger.info("Ip <ip>" + ip + "</ip> has maximum traffic: <traf>" + aggregator.getUserStat(ip) + "</traf> ");
    }
}
