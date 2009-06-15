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
        new Thread(new Download()).start();
        new Thread(new Parsing()).start();
    }

    public static void startTestPerformance() {
        new TestQueue().run();
        new TestNotify().run();
    }
}
