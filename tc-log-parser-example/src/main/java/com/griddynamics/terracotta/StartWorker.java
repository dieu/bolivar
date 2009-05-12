package com.griddynamics.terracotta;


public class StartWorker {

    /**
     * Starts one worker.
     *
     * @param args - input arguments.
     * @throws Exception - (new JobService()).startWorker()'s exception.
     */
    public static void main(String[] args) throws Exception {
        Thread.sleep(2000L);
        (new JobService()).startWorker();
    }
}
