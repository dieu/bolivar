package com.griddynamics.terracotta.worker;

import com.griddynamics.terracotta.worker.factory.Download;
import com.griddynamics.terracotta.worker.factory.Parsing;
import com.griddynamics.terracotta.worker.factory.TestQueue;
import com.griddynamics.terracotta.worker.factory.TestNotify;

/**
 * @author apanasenko aka dieu
 *         Date: 04.06.2009
 *         Time: 16:47:45
 */
public class PhaseFactory {
    public static void startLogParsing() {
        new Download().run();
        new Parsing().run();
    }

    public static void startTestPerformance() {
        /*try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }*/
        //new TestQueue().run();
        new TestNotify().run();
    }
}
