package com.griddynamics.terracotta.parser.separate;

import org.apache.log4j.Logger;
import static com.griddynamics.terracotta.util.StrUtil.encloseWithTag;

import java.net.InetAddress;

/**
 * @author agorbunov @ 26.05.2009 18:40:30
 */
// TODO Measure the duration of each phase
// TODO Merge with SchedulerMeter, and extract common steps to a superclass
public class Tracker {
    public static enum Phase {
        REMOVING,
        DOWNLOADING,
        PARSING,
        RETURNING,
        DONE,
        ERROR
    }

    private static Logger logger = Logger.getLogger(Tracker.class);
    private String phase;

    public void entered(Phase phase) {
        this.phase = phase.toString();
        printWorkerIdEnclosedWithPhase();
    }

    private void printWorkerIdEnclosedWithPhase() {
        print(message());
    }

    private String message() {
        return encloseWithTag(workerId(), phase());
    }

    private String workerId() {
        try {
            return host();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String host() throws Exception {
        return InetAddress.getLocalHost().toString();
    }

    private String phase() {
        return status() + phaseId();
    }

    private String status() {
        return "s";
    }

    private String phaseId() {
        String lowercasedPhase = phase.toLowerCase();
        return "" + lowercasedPhase.charAt(0) + lowercasedPhase.charAt(2);
    }

    private void print(String s) {
        logger.info(s);
    }
}
