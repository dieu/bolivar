package com.griddynamics.terracotta;

import com.griddynamics.terracotta.worker.Worker;


public class StartWorker {

    public static void main(String[] args) throws Exception {
        new Worker().run();
    }
}
