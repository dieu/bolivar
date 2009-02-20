package com.griddynamics.terracotta;

import org.springframework.context.support.GenericApplicationContext;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParameter;
import org.terracotta.message.routing.Router;
import org.terracotta.message.routing.RoundRobinRouter;
import org.terracotta.workmanager.statik.StaticWorkManager;

import java.util.HashMap;


public class Example {

    public void startWorker(String id) throws Exception {
        GenericApplicationContext context = getContext();
        BatchWorker worker = new BatchWorker("testTopology", id,context);
        worker.start();
    }

    private GenericApplicationContext getContext() {
        GenericApplicationContext context = new GenericApplicationContext();
        XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(context);
        xmlReader.loadBeanDefinitions(new ClassPathResource("test-work-context.xml"));
        context.refresh();
        return context;
    }


    public void startWorkers(String... ids) throws Exception {
        GenericApplicationContext context = getContext();

        for(String id:ids){
            BatchWorker worker = new BatchWorker("testTopology", id,context);
            worker.start();
        }
    }


    public void lunchJob(Long startId,String... workerIds){
        StaticWorkManager staticWorkManager = new StaticWorkManager("testTopology", new RoundRobinRouter(), workerIds);

        for(int i = 0; i < 60; i++){
            HashMap<String, JobParameter> map = new HashMap<String, JobParameter>();
            map.put("test",new JobParameter(i+startId));
            BatchWork work = new BatchWork("testJob", new JobParameters(map));
            staticWorkManager.schedule(work);
        }
    }
}
