package com.griddynamics.terracotta;

import com.griddynamics.terracotta.worker.factory.Parsing;
import com.griddynamics.terracotta.worker.Worker;
import com.griddynamics.terracotta.scheduler.Scheduler;

import java.io.IOException;

public class JobService {
    /**
     * Starts the worker.
     *
     * @throws Exception - rethrown worker.start() exception.
     */
    public void startWorker(String typeOfWork) throws Exception {
        new Worker(typeOfWork).run();
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
        new Scheduler(countWorkers, dir, dirUrl, localDir).start();
    }
}
