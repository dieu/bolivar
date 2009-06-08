package com.griddynamics.terracotta.scheduler;

import commonj.work.Work;

/**
 * @author: apanasenko aka dieu
* Date: 03.06.2009
* Time: 15:23:43
*/
public interface ForEachLog {
    public Work work(String log);
    public Phase phase();
}
