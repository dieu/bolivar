package com.griddynamics.terracotta;

import org.terracotta.message.routing.RoundRobinRouter;
import org.terracotta.workmanager.dynamic.DynamicWorkManager;
import org.terracotta.workmanager.dynamic.DynamicWorkerFactory;
import org.terracotta.workmanager.dynamic.DynamicWorker;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.io.File;
import java.io.IOException;

import commonj.work.WorkItem;
import com.griddynamics.terracotta.parser.ParserLogWork;
import com.griddynamics.terracotta.parser.Aggregator;


public class Example {
    private String topologyName = "parserTopology";

    public void startWorker() throws Exception {
        DynamicWorkerFactory dynamicWorkerFactory = new DynamicWorkerFactory(topologyName, null, Executors.newScheduledThreadPool(4));
        DynamicWorker worker = dynamicWorkerFactory.create();
        worker.start();
    }

    public void lunchJob(String dir,String dirUrl) throws InterruptedException, IOException {
        DynamicWorkManager workManager = new DynamicWorkManager(topologyName,null,new RoundRobinRouter());
        List<WorkItem> workItems = new ArrayList<WorkItem>();
        File file = new File(dir);
        if (!file.exists()) throw new IOException("Directory not exists");
        if (!file.isDirectory()) throw new IOException("It is not directory");

        Long start = System.currentTimeMillis();
        Aggregator aggregator = new Aggregator();

        for(String fileName:file.list()){
            System.out.println("Sheduler work for " + dirUrl + fileName);
            ParserLogWork work = new ParserLogWork(dirUrl + fileName,aggregator);
            WorkItem workItem = workManager.schedule(work);
            workItems.add(workItem);
        }

        workManager.waitForAll(workItems,Long.MAX_VALUE);
        String ip = aggregator.getMaxIp();
        System.out.println("Finished work in " + (System.currentTimeMillis() - start));
        System.out.println("Ip " + ip + " has maximal traffic " + aggregator.getUserStat(ip));
    }
}
