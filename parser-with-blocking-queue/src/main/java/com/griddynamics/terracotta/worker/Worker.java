package com.griddynamics.terracotta.worker;

import com.griddynamics.terracotta.worker.factory.Download;
import com.griddynamics.terracotta.worker.factory.Parsing;

/**
 * @author apanasenko aka dieu
 * Date: 04.06.2009
 * Time: 13:18:24
 */
public class Worker implements Runnable {

    public void run() {
        new Thread(new Download()).start();
        new Thread(new Parsing()).start();
    }
}
