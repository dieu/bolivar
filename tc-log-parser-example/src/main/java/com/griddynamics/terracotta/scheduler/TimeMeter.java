package com.griddynamics.terracotta.scheduler;

import org.apache.log4j.Logger;
import static org.junit.Assert.*;

import java.util.Map;
import java.util.HashMap;

import com.griddynamics.terracotta.util.StrUtil;

/**
 * @author agorbunov @ 20.05.2009 14:15:22
 */
public class TimeMeter {
    private enum Phase {
        REMOVING,
        DOWNLOADING,
        PARSING,
        AGGREGATING;
        public String shortName() {
            return name().toLowerCase().substring(0, 3);
        }
    }

    private static final Logger logger = Logger.getLogger(TimeMeter.class);
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
            reportTotal();
        }
    }

    private String formatDuration() {
        return StrUtil.encloseWithTag(duration, phase.shortName());
    }

    private boolean isLastPhase() {
        return phase == lastPhase();
    }

    private void reportTotal() {
        verifyPassedAllPhases();
        logger.info("Finished analysis in " + formatTotal());
    }

    private void verifyPassedAllPhases() {
        assertEquals(allPhases().length, phaseDuration.size());
    }

    private String formatTotal() {
        return StrUtil.encloseWithTag(total(), "to");
    }

    private Long total() {
        return sum(Phase.DOWNLOADING, Phase.PARSING, Phase.AGGREGATING);
    }

    private Long sum(Phase... phases) {
        Long sum = 0L;
        for (Phase phase : phases)
            sum += phaseDuration.get(phase);
        return sum;
    }

    private Phase lastPhase() {
        return allPhases()[(allPhases().length - 1)];
    }

    private Phase[] allPhases() {
        return Phase.values();
    }
}
