package com.griddynamics.terracotta.scheduler;

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
import com.griddynamics.terracotta.parser.separate.ParseLogs.AveragePerformance;
import com.griddynamics.terracotta.util.ThreadUtil;
import com.griddynamics.terracotta.util.FileUtil;
import static com.griddynamics.terracotta.util.StrUtil.encloseWithTag;
import com.griddynamics.terracotta.scheduler.TimeMeter;
import com.griddynamics.terracotta.JobService;
import commonj.work.WorkItem;
import commonj.work.Work;

/**
 * @author agorbunov @ 07.05.2009 18:12:31
 */
public class Scheduler {
    private static final Long SECOND = 1000L;
    private static Logger logger = Logger.getLogger(Scheduler.class);
    private TimeMeter timeMeter = new TimeMeter();
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
        workItems.add(workManager.schedule(work));
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
        String ip = aggregator.ipWithMaxTraffic();
        logger.info("Ip <ip>" + ip + "</ip> has maximum traffic: <traf>" + aggregator.traffic(ip) + "</traf> ");
        timeMeter.done();
    }

    private void reportParsingPerformance() {
        AveragePerformance ap = aggregator.averagePerformance();
        logger.info("Parsing performance:");
        logger.info("Parsed: " + encloseWithTag(ap.parsed(), "op"));
        logger.info("Parsed min: " + ap.parsedMin());
        logger.info("Parsed max: " + ap.parsedMax());
        logger.info("Parsed one: " + ap.parsedOne());
        logger.info("Returned: " + encloseWithTag(ap.returned(), "or"));
        logger.info("Returned min: " + ap.returnedMin());
        logger.info("Returned max: " + ap.returnedMax());
        logger.info("Logs: " + ap.logs());
    }
}
