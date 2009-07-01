package com.griddynamics.terracotta.helpers;

import org.terracotta.modules.concurrent.collections.ConcurrentStringMap;
import org.apache.log4j.Logger;

/**
 * @author apanasenko aka Dieu
 * Date: 30.06.2009
 * Time: 13:40:00
 */
public class WaitPipe implements Runnable {
    private static Logger logger = Logger.getLogger(WaitPipe.class);
    private Pipe<ConcurrentStringMap<Long>> pipe;

    public WaitPipe(Pipe<ConcurrentStringMap<Long>> pipe) {
        this.pipe = pipe;
    }

    public void run() {
        synchronized (pipe) {
            try {
                if(pipe.getPipe().isEmpty()) {
                    logger.info("Start wait");
                    pipe.wait();
                    logger.info("End wait");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
