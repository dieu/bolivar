package com.griddynamics.terracotta;

import org.apache.log4j.Logger;
import static org.junit.Assert.*;

import java.util.Map;
import java.util.HashMap;

import com.griddynamics.terracotta.util.StrUtil;

/**
 * @author agorbunov @ 20.05.2009 14:15:22
 */
public class SchedulerMeter {
    private enum Phase {
        REMOVING,
        DOWNLOADING,
        PARSING,
        AGGREGATING;

        public String toString() {
            return name().toLowerCase();
        }
    }

    private static final Logger logger = Logger.getLogger(SchedulerMeter.class);
    private Map<Phase, Long> phaseDuration = new HashMap<Phase, Long>();
    private boolean isMeasuring;
    private Phase phase;
    private Long started;
    private Long duration;

    public void removing() {
        begin(Phase.REMOVING);
    }

    public void downloading() {
        begin(Phase.DOWNLOADING);
    }

    public void parsing() {
        begin(Phase.PARSING);
    }

    public void aggregating() {
        begin(Phase.AGGREGATING);
    }

    private void begin(Phase phase) {
        assertFalse(isMeasuring);
        isMeasuring = true;
        started = System.currentTimeMillis();
        this.phase = phase;
        logger.info("Started " + phase);
    }

    public void done() {
        end();
    }

    private void end() {
        assertTrue(isMeasuring);
        isMeasuring = false;
        duration = System.currentTimeMillis() - started;
        phaseDuration.put(phase, duration);
        logger.info("Finished " + phase + " in " + formatDuration());
        if (isLastPhase()) {
            reportTotalDuration();
        }
    }

    private String formatDuration() {
        String phaseTag = phase.toString().substring(0, 2);
        return StrUtil.encloseWithTag(duration, phaseTag);
    }

    private boolean isLastPhase() {
        return phase == Phase.AGGREGATING;
    }

    private void reportTotalDuration() {
        assertEquals(Phase.values().length, phaseDuration.size());
        Long totalDuration = 0L;
        for (Phase phase : phaseDuration.keySet())
            totalDuration += phaseDuration.get(phase);
        String formattedTotalDuration = StrUtil.encloseWithTag(totalDuration, "to");
        logger.info("Finished analysis in " + formattedTotalDuration);
    }
}
