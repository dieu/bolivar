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
    private static final Logger logger = Logger.getLogger(TimeMeter.class);
    private Map<Phase, Long> phaseDuration = new HashMap<Phase, Long>();
    private boolean isMeasuring;
    private Phase phase;
    private Long started;
    private Long duration;

    public void start(Phase phase) {
        this.phase = phase;
        reportPhase();
        startMeasuring();
    }

    private void reportPhase() {
        logger.info("Started " + phase);
    }

    private void startMeasuring() {
        assertFalse(isMeasuring);
        isMeasuring = true;
        started = System.currentTimeMillis();
    }

    public void stop() {
        stopMeasuring();
        reportDuration();
        if (isLastPhase())
            reportTotal();
    }

    private void stopMeasuring() {
        assertTrue(isMeasuring);
        isMeasuring = false;
        duration = System.currentTimeMillis() - started;
        phaseDuration.put(phase, duration);
    }

    private void reportDuration() {
        logger.info("Finished " + phase + " in " + formatDuration());
    }

    private String formatDuration() {
        return StrUtil.encloseWithTag(duration, phase.shortName());
    }

    private boolean isLastPhase() {
        return phase == lastPhase();
    }

    private Phase lastPhase() {
        return allPhases()[(allPhases().length - 1)];
    }

    private Phase[] allPhases() {
        return Phase.values();
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
        return sum(Phase.PARSING, Phase.AGGREGATING);
    }

    private Long sum(Phase... phases) {
        Long sum = 0L;
        for (Phase phase : phases)
            sum += phaseDuration.get(phase);
        return sum;
    }
}
