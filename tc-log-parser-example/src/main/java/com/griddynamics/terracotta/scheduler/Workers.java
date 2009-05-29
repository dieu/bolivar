package com.griddynamics.terracotta.scheduler;

import org.terracotta.workmanager.dynamic.DynamicWorkManager;
import org.terracotta.message.routing.RoundRobinRouter;
import org.apache.log4j.Logger;
import com.griddynamics.terracotta.JobService;
import com.griddynamics.terracotta.util.ThreadUtil;
import com.griddynamics.terracotta.util.FileUtil;
import commonj.work.Work;
import commonj.work.WorkItem;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import static com.griddynamics.terracotta.scheduler.TimeMeter.Phase;

/**
 * @author agorbunov @ 29.05.2009 17:00:07
 */
public class Workers {
    public static interface Measurement {
        // No methods - a marker interface.
    }
    public static interface PerLog extends Measurement {
        public Work work(String log);
        public Phase phase();
    }
    public static interface PerWorker extends Measurement {
        public Work work();
        public Phase phase();
    }

    private static final Long SECOND = 1000L;
    private static Logger logger = Logger.getLogger(Workers.class);
    private DynamicWorkManager workManager;
    private List<WorkItem> workItems = new ArrayList<WorkItem>();
    private PerLog measurements;
    private TimeMeter timeMeter;
    private String masterDir;

    public Workers(String masterDir, TimeMeter timeMeter) {
        this.masterDir = masterDir;
        this.timeMeter = timeMeter;
    }

    public void setup() {
        connectToWorkers();
    }

    private void connectToWorkers() {
        requestConnection();
        waitUntilAllWorkersConnected();
    }

    public void requestConnection() {
        workManager = new DynamicWorkManager(JobService.topologyName, null, new RoundRobinRouter());
    }

    public void waitUntilAllWorkersConnected() {
        logger.info("Waiting for workers...");
        ThreadUtil.sleep(30 * SECOND);
    }

    public void perform(PerLog measurements) {
        this.measurements = measurements;
        timeMeter.start(measurements.phase());
        scheduleAllLogs();
        waitForWorkers();
        timeMeter.done();
    }

    private void scheduleAllLogs() {
        clearWorkItems();
        for (String log : logs())
            scheduleOneLog(log);
    }

    private void clearWorkItems() {
        workItems.clear();
    }

    private String[] logs() {
        File logs = new File(masterDir);
        FileUtil.verifyDirExists(logs);
        return logs.list();
    }

    private void scheduleOneLog(String log) {
        Work work = measurements.work(log);
        schedule(work);
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
}
