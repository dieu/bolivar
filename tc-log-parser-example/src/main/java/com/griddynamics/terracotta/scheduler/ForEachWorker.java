package com.griddynamics.terracotta.scheduler;

import commonj.work.Work;

/**
 * @author: apanasenko aka dieu
* Date: 03.06.2009
* Time: 15:23:48
*/
public interface ForEachWorker {
    public Work work();
    public Phase phase();
}
