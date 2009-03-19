package com.griddynamics.terracotta;

import com.griddynamics.terracotta.parser.Aggregator;
import com.griddynamics.terracotta.parser.ParserLogWork;
import commonj.work.WorkItem;
import org.apache.log4j.Logger;
import org.terracotta.message.routing.RoundRobinRouter;
import org.terracotta.workmanager.dynamic.DynamicWorkManager;
import org.terracotta.workmanager.dynamic.DynamicWorker;
import org.terracotta.workmanager.dynamic.DynamicWorkerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class JobService {
    private static final Logger logger = Logger.getLogger(JobService.class);
    private final String topologyName = "parserTopology";

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
    public void lunchJob(String dir, String dirUrl) throws InterruptedException, IOException {
        DynamicWorkManager workManager = new DynamicWorkManager(topologyName, null, new RoundRobinRouter());

        logger.info("Waiting for workers...");
        Thread.sleep(30000L);

        Long startTime = System.currentTimeMillis();

        Aggregator aggregator = new Aggregator();

        logger.info("Generating work items list...");
        List<WorkItem> workItems = new ArrayList<WorkItem>();

        File file = new File(dir);
        checkDirectory(dir, file);

        for (String fileName : file.list()) {
            String pathToFile = dirUrl + fileName;
            logger.info("Sheduled work for " + pathToFile);
            workItems.add(workManager.schedule(new ParserLogWork(pathToFile, aggregator)));
        }
        workManager.waitForAll(workItems, Long.MAX_VALUE);

        String ip = aggregator.getIpWithMaxTraffic();
        logger.info("Work finished in " + (System.currentTimeMillis() - startTime));
        logger.info("Ip " + ip + " has maximum traffic: " + aggregator.getUserStat(ip));
    }

    private void checkDirectory(String dir, File file) throws IOException {
        if (!file.exists()) throw new IOException("Directory " + dir + " not exists!");
        if (!file.isDirectory()) throw new IOException(dir + " is not directory!");
    }
}
