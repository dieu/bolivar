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
    public static enum Phase {
        REMOVING("rem"),
        DOWNLOADING("dow"),
        PARSING("par"),
        RETURNING("ret"),
        DONE("fin"),
        ERROR("err");
        public final String tag;
        Phase(String tag) {
            this.tag = tag;
        }
    }

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
        return encloseWithTag(workerId(), phase());
    }

    private String workerId() {
        try {
            return NetUtil.host();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String phase() {
        return phase.tag;
    }

    private void print(String s) {
        logger.info(s);
    }
}
