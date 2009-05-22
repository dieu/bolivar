package com.griddynamics.terracotta;

import org.terracotta.workmanager.dynamic.DynamicWorkManager;
import org.terracotta.message.routing.RoundRobinRouter;
import org.apache.log4j.Logger;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.List;
import java.util.ArrayList;

import com.griddynamics.terracotta.parser.Aggregator;
import com.griddynamics.terracotta.parser.separate_downloading.RemoveLogs;
import com.griddynamics.terracotta.parser.separate_downloading.DownloadLog;
import com.griddynamics.terracotta.parser.separate_downloading.ParseLogs;
import com.griddynamics.terracotta.util.ThreadUtil;
import com.griddynamics.terracotta.util.FileUtil;
import static com.griddynamics.terracotta.util.StrUtil.encloseWithTag;
import commonj.work.WorkItem;
import commonj.work.Work;

/**
 * @author agorbunov @ 07.05.2009 18:12:31
 */
public class Scheduler {
    private static final Long SECOND = 1000L;
    private static Logger logger = Logger.getLogger(Scheduler.class);
    private SchedulerMeter timeMeter = new SchedulerMeter();
    private String masterDir;
    private String masterUrl;
    private String workerDir;
    private DynamicWorkManager workManager;
    private List<WorkItem> workItems = new ArrayList<WorkItem>();
    private Aggregator aggregator;

    public Scheduler(String masterDir, String masterUrl, String workerDir) {
        this.masterDir = masterDir;
        this.masterUrl = masterUrl;
        this.workerDir = workerDir;
        assertNotNull(masterDir);
        assertNotNull(masterUrl);
        assertNotNull(workerDir);
    }

    public void findMaxTrafficWithSeveralWorkers() {
        connectToWorkers();
        findMaxTraffic();
        reportParsingPerformance();
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

    private void findMaxTraffic() {
        findTrafficOfEachIp();
        findMostConsumingIp();
    }

    private void findTrafficOfEachIp() {
        fetchLogs();
        parseLogs();
    }

    private void fetchLogs() {
        removeLogs();
        downloadLogs();
    }

    private void removeLogs() {
        timeMeter.removing();
        scheduleRemoving();
        waitForWorkers();
        timeMeter.done();
    }

    private void scheduleRemoving() {
        clearWorkItems();
        // TODO Schedule the task according to the number of workers, rather than the number of logs
        for (String log : logs())
            scheduleRemovingOne();
    }

    private void clearWorkItems() {
        workItems.clear();
    }

    private String[] logs() {
        File logs = new File(masterDir);
        FileUtil.verifyDirExists(logs);
        return logs.list();
    }

    private void scheduleRemovingOne() {
        schedule(RemoveLogs.from(workerDir));
    }

    private void schedule(Work work) {
        WorkItem workItem = workManager.schedule(work);
        workItems.add(workItem);
    }

    private void waitForWorkers() {
        try {
            workManager.waitForAll(workItems, Long.MAX_VALUE);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void downloadLogs() {
        timeMeter.downloading();
        scheduleDownloading();
        waitForWorkers();
        timeMeter.done();
    }

    private void scheduleDownloading() {
        clearWorkItems();
        for (String log : logs())
            scheduleDownloadingOne(log);
    }

    private void scheduleDownloadingOne(String log) {
        String url = masterUrl + log;
        schedule(DownloadLog.fromTo(url, workerDir));
    }

    private void parseLogs() {
        timeMeter.parsing();
        scheduleParsing();
        waitForWorkers();
        timeMeter.done();
    }

    private void scheduleParsing() {
        aggregator = new Aggregator();
        clearWorkItems();
        // TODO Schedule the task according to the number of workers, rather than the number of logs
        for (String log : logs())
            scheduleParsingOne();
    }

    private void scheduleParsingOne() {
        schedule(ParseLogs.inUsing(workerDir, aggregator));
    }

    private void findMostConsumingIp() {
        timeMeter.aggregating();
        String ip = aggregator.getIpWithMaxTraffic();
        logger.info("Ip <ip>" + ip + "</ip> has maximum traffic: <traf>" + aggregator.getUserStat(ip) + "</traf> ");
        timeMeter.done();
    }

    private void reportParsingPerformance() {
        ParseLogs.Performance p = aggregator.averageParsingPerformance();
        logger.info("One worker parsed in " + encloseWithTag(p.parsedIn, "op"));
        logger.info("One worker returned in " + encloseWithTag(p.returnedIn, "or"));
    }
}
