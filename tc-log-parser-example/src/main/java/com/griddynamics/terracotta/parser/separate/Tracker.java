package com.griddynamics.terracotta.parser.separate;

import org.apache.log4j.Logger;
import static com.griddynamics.terracotta.util.StrUtil.encloseWithTag;
import com.griddynamics.terracotta.util.NetUtil;

/**
 * @author agorbunov @ 26.05.2009 18:40:30
 */
// TODO Measure the duration of each phase
// TODO Merge with SchedulerMeter, and extract common steps to a superclass
public class Tracker {
    private static Logger logger = Logger.getLogger(Tracker.class);
    private Phase phase;

    public void phase(Phase phase) {
        if (!isNew(phase))
            return;
        set(phase);
        printWorkerIdEnclosedWithPhase();
    }

    private boolean isNew(Phase phase) {
        return phase != this.phase;
    }

    private void set(Phase phase) {
        this.phase = phase;
    }

    private void printWorkerIdEnclosedWithPhase() {
        print(message());
    }

    private String message() {
        return encloseWithTag(NetUtil.worker(), phase());
    }

    private String phase() {
        return phase.tag;
    }

    private void print(String s) {
        logger.info(s);
    }
}
