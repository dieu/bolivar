package com.griddynamics.terracotta;

import org.terracotta.workmanager.statik.StaticWorkManager;
import org.terracotta.workmanager.statik.StaticWorker;
import org.terracotta.workmanager.DefaultWorkItem;
import org.terracotta.message.topology.Topology;
import org.terracotta.masterworker.WorkMessage;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.concurrent.ExecutorService;


public class BatchWorker extends StaticWorker {

    transient ConfigurableApplicationContext context;

    public BatchWorker(String s, String s1, ConfigurableApplicationContext context) {
        super(s, s1);
        this.context = context;
    }

    public BatchWorker(String s, String s1, ExecutorService executorService, ConfigurableApplicationContext context) {
        super(s, s1, executorService);
        this.context = context;
    }

    public BatchWorker(String s, Topology.Factory factory, String s1, ConfigurableApplicationContext context) {
        super(s, factory, s1);
        this.context = context;
    }

    public BatchWorker(String s, Topology.Factory factory, String s1, ExecutorService executorService, ConfigurableApplicationContext context) {
        super(s, factory, s1, executorService);
        this.context = context;
    }

    @Override
    protected void doExecute(WorkMessage<DefaultWorkItem> defaultWorkItemWorkMessage) {
        BatchWork work = (BatchWork) defaultWorkItemWorkMessage.getWorkObject().getResult();
        work.setContext(context);
        super.doExecute(defaultWorkItemWorkMessage);
    }
}
