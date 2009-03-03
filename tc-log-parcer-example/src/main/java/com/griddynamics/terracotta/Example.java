package com.griddynamics.terracotta;

import org.springframework.context.support.GenericApplicationContext;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParameter;
import org.terracotta.message.routing.Router;
import org.terracotta.message.routing.RoundRobinRouter;
import org.terracotta.workmanager.statik.StaticWorkManager;
import org.terracotta.workmanager.statik.StaticWorker;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import commonj.work.Work;
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


    public void lunchJob(Long startId,String... workerIds) throws InterruptedException {
        StaticWorkManager staticWorkManager = new StaticWorkManager("testTopology", new RoundRobinRouter(), workerIds);

        List<ParserLogWork> workList = new ArrayList<ParserLogWork>();
        List<WorkItem> workItems = new ArrayList<WorkItem>();
        for(int i = 0; i < 60; i++){
            HashMap<String, JobParameter> map = new HashMap<String, JobParameter>();
            map.put("test",new JobParameter(i+startId));
            ParserLogWork work = new ParserLogWork("http://localhost/vtt-traf");
            WorkItem workItem = staticWorkManager.schedule(work);
            workList.add(work);
            workItems.add(workItem);
        }
        staticWorkManager.waitForAll(workItems,Long.MAX_VALUE);
        for(ParserLogWork parserLogWork:workList){
            System.out.println(" " + parserLogWork.getResult());
        }
    }
}
