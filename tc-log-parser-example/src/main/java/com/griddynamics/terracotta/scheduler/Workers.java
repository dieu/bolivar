package com.griddynamics.terracotta.scheduler;

import org.terracotta.workmanager.dynamic.DynamicWorkManager;
import org.terracotta.message.routing.RoundRobinRouter;
import org.terracotta.modules.concurrent.collections.ConcurrentStringMap;
import org.apache.log4j.Logger;
import com.griddynamics.terracotta.JobService;
import com.griddynamics.terracotta.parser.separate.Count;
import com.griddynamics.terracotta.util.ThreadUtil;
import com.griddynamics.terracotta.util.FileUtil;
import com.griddynamics.terracotta.util.NetUtil;
import commonj.work.Work;
import commonj.work.WorkItem;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import com.griddynamics.terracotta.scheduler.TimeMeter.Phase;
import static com.griddynamics.terracotta.scheduler.TimeMeter.Phase.COUNTING;

/**
 * @author agorbunov @ 29.05.2009 17:00:07
 */
public class Workers {
    public static interface Measurement {
        // No methods - a marker interface.
    }
    public static interface ForEachLog extends Measurement {
        public Work work(String log);
        public Phase phase();
    }
    public static interface ForEachWorker extends Measurement {
        public Work work();
        public Phase phase();
    }

    public static class Count implements Work {
        private Map<String, Boolean> machines;
        public Count(Map<String, Boolean> machines) {
            this.machines = machines;
        }
        public void run() {
            machines.put(NetUtil.worker(), Boolean.TRUE);
        }
        public boolean isDaemon() {
            return false;
        }
        public void release() {
            // Do nothing.
        }
    }

    private static final Long SECOND = 1000L;
    private static Logger logger = Logger.getLogger(Workers.class);
    private DynamicWorkManager workManager;
    private List<WorkItem> workItems = new ArrayList<WorkItem>();
    private Map<String, Boolean> machines = new ConcurrentStringMap<Boolean>();
    private ForEachLog measurement;
    private TimeMeter timeMeter;
    private String masterDir;

    public Workers(String masterDir, TimeMeter timeMeter) {
        this.masterDir = masterDir;
        this.timeMeter = timeMeter;
    }

    public void setup() {
        connectToWorkers();
        countWorkers();
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

    private void countWorkers() {
        perform(new ForEachLog() {
            public Work work(String log) {
                return new Count(machines);
            }
            public Phase phase() {
                return COUNTING;
            }
        });
        reportWorkerCount();
    }

    private void reportWorkerCount() {
        logger.info("Found " + machines.size() + " workers");
    }

    public void perform(ForEachLog measurements) {
        this.measurement = measurements;
        timeMeter.start(measurements.phase());
        scheduleAllLogs();
        waitForWorkers();
        timeMeter.stop();
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
        Work work = measurement.work(log);
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
