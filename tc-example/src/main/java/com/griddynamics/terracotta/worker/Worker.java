package com.griddynamics.terracotta.worker;

import com.griddynamics.terracotta.worker.factory.Download;
import com.griddynamics.terracotta.worker.factory.Parsing;

/**
 * @author: apanasenko aka dieu
 * Date: 04.06.2009
 * Time: 13:18:24
 */
public class Worker implements Runnable {
    private String typeOfWork;

    public Worker(String typeOfWork) {
        this.typeOfWork = typeOfWork;
    }

    public void run() {
        try {
            Thread.sleep(30000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(typeOfWork.equals("parsing")) {
            PhaseFactory.startLogParsing();
        } else if(typeOfWork.equals("test")) {
            PhaseFactory.startTestPerformance();
        }
    }
}
