package com.griddynamics.terracotta;

import org.terracotta.message.routing.RoundRobinRouter;
import org.terracotta.workmanager.dynamic.DynamicWorkManager;
import org.terracotta.workmanager.dynamic.DynamicWorkerFactory;
import org.terracotta.workmanager.dynamic.DynamicWorker;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.io.File;
import java.io.IOException;

import commonj.work.WorkItem;
import com.griddynamics.terracotta.parser.ParserLogWork;
import com.griddynamics.terracotta.parser.Aggregator;


public class Example {
    private static final Logger logger = Logger.getLogger(Example.class);
    private String topologyName = "parserTopology";

    public void startWorker() throws Exception {
        DynamicWorkerFactory dynamicWorkerFactory = new DynamicWorkerFactory(topologyName, null, Executors.newScheduledThreadPool(1));
        DynamicWorker worker = dynamicWorkerFactory.create();
        worker.start();
    }

    public void lunchJob(String dir,String dirUrl) throws InterruptedException, IOException {
        DynamicWorkManager workManager = new DynamicWorkManager(topologyName,null,new RoundRobinRouter());
        List<WorkItem> workItems = new ArrayList<WorkItem>();
        
        File file = new File(dir);
        if (!file.exists()) throw new IOException("Directory not exists");
        if (!file.isDirectory()) throw new IOException("It is not directory");
        logger.info("Wait for workers");
        Thread.sleep(30000L);


        Long start = System.currentTimeMillis();
        Aggregator aggregator = new Aggregator();
        logger.info("Start work");
        for(String fileName:file.list()){
            logger.info("Sheduler work for " + dirUrl + fileName);
            ParserLogWork work = new ParserLogWork(dirUrl + fileName,aggregator);
            WorkItem workItem = workManager.schedule(work);
            workItems.add(workItem);
        }

        workManager.waitForAll(workItems,Long.MAX_VALUE);
        String ip = aggregator.getMaxIp();
        logger.info("Finished work in " + (System.currentTimeMillis() - start));
        logger.info("Ip " + ip + " has maximal traffic " + aggregator.getUserStat(ip));
    }
}
