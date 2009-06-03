package com.griddynamics.terracotta.scheduler;

import com.griddynamics.terracotta.JobService;
import com.griddynamics.terracotta.util.FileUtil;
import com.griddynamics.terracotta.util.ThreadUtil;
import commonj.work.Work;
import commonj.work.WorkItem;
import org.apache.log4j.Logger;
import org.terracotta.message.routing.RoundRobinRouter;
import org.terracotta.workmanager.dynamic.DynamicWorkManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author agorbunov @ 29.05.2009 17:00:07
 */
public class Workers {

    //    public static class Count implements Work {
//        private Map<String, Boolean> machines;
//        public Count(Map<String, Boolean> machines) {
//            this.machines = machines;
//        }
//        public void run() {
//            machines.put(NetUtil.worker(), Boolean.TRUE);
//        }
//        public boolean isDaemon() {
//            return false;
//        }
//        public void release() {
//            // Do nothing.
//        }
//    }

    private static final Long SECOND = 1000L;
    private static Logger logger = Logger.getLogger(Workers.class);
    private DynamicWorkManager workManager;
    private List<WorkItem> workItems = new ArrayList<WorkItem>();
//    private Map<String, Boolean> machines = new ConcurrentStringMap<Boolean>();
    private int countWorkers;
    private ForEachLog measurement;
    private ForEachWorker measurementForEachWorker;
    private TimeMeter timeMeter;
    private String masterDir;

    public Workers(String masterDir, TimeMeter timeMeter, int countWorkers) {
        this.masterDir = masterDir;
        this.timeMeter = timeMeter;
        this.countWorkers = countWorkers;
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
//        perform(new ForEachLog() {
//            public Work work(String log) {
//                return new Count(machines);
//            }
//            public Phase phase() {
//                return COUNTING;
//            }
//        });
        reportCount();
    }

    private void reportCount() {
        logger.info("Found " + countWorkers /*machines.size()*/ + " workers");
    }

    public void perform(ForEachLog measurement) {
        this.measurement = measurement;
        timeMeter.start(measurement.phase());
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

    // Schedule for each log instead - this seems unreliable.
    @Deprecated
    public void perform(ForEachWorker measurement) {
        measurementForEachWorker = measurement;
        timeMeter.start(measurement.phase());
        scheduleAllWorkers();
        waitForWorkers();
        timeMeter.stop();
    }

    private void scheduleAllWorkers() {
        clearWorkItems();
        for (int i = 0; i < countWorkers /* machines.size() */; i++)
            scheduleOneWorker();
    }

    private void scheduleOneWorker() {
        Work work = measurementForEachWorker.work();
        schedule(work);
    }
}
