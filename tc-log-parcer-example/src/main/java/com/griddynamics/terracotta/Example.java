package com.griddynamics.terracotta;

import org.terracotta.message.routing.RoundRobinRouter;
import org.terracotta.workmanager.statik.StaticWorkManager;
import org.terracotta.workmanager.statik.StaticWorker;

import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;

import commonj.work.WorkItem;


public class Example {

    public void startWorker(String id) throws Exception {
        StaticWorker worker = new StaticWorker("testTopology", id);
        worker.start();
    }

    public void startWorkers(String... ids) throws Exception {
        for(String id:ids){
            startWorker(id);
        }
    }


    public void lunchJob(String... workerIds) throws InterruptedException, IOException {
        StaticWorkManager staticWorkManager = new StaticWorkManager("testTopology", new RoundRobinRouter(), workerIds);

        List<ParserLogWorkOld> workList = new ArrayList<ParserLogWorkOld>();
        List<WorkItem> workItems = new ArrayList<WorkItem>();
        File file = new File("/var/www/logs");
        if (!file.exists()) throw new IOException("Directory not exists");
        if (!file.isDirectory()) throw new IOException("It is not directory");

        Aggregator aggregator = new Aggregator();

        for(String fileName:file.list()){
            System.out.println("Sheduler work for " + "http://localhost/logs/" + fileName);
            ParserLogWorkOld work = new ParserLogWorkOld("http://localhost/logs/" + fileName,aggregator);
            WorkItem workItem = staticWorkManager.schedule(work);
            workList.add(work);
            workItems.add(workItem);
        }

        staticWorkManager.waitForAll(workItems,Long.MAX_VALUE);
        System.out.println("aggregation " + aggregator);
    }
}
