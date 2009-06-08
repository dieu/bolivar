package com.griddynamics.terracotta;

import com.griddynamics.terracotta.scheduler.Scheduler;
import org.terracotta.workmanager.dynamic.DynamicWorker;
import org.terracotta.workmanager.dynamic.DynamicWorkerFactory;

import java.io.IOException;
import java.util.concurrent.Executors;

public class JobService {
    public static final String topologyName = "parserTopology";

    /**
     * Starts the worker.
     *
     * @throws Exception - rethrown worker.start() exception.
     */
    public void startWorker() throws Exception {
        DynamicWorkerFactory dynamicWorkerFactory = new DynamicWorkerFactory(topologyName, null, Executors.newScheduledThreadPool(1));
        DynamicWorker worker = dynamicWorkerFactory.create();
        worker.start();
    }

    /**
     * Launches parser jobs with given url and directory name.
     *
     * @param dir    - directory name where log files is located.
     * @param dirUrl - url of directory.
     * @throws InterruptedException - workManager.waitForAll()'s exception.
     * @throws IOException          - checkDirectory()'s exception. Thrown when directory doesn't exists or provided location is not a directory
     */
    public void lunchJob(String dir, String dirUrl, String localDir, String countWorkers) throws InterruptedException, IOException {
        new Scheduler(dir, dirUrl, localDir, countWorkers).findMaxTrafficWithSeveralWorkers();
    }
}
