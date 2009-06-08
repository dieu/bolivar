package com.griddynamics.terracotta.parser.separate;

import java.util.Collection;
import static java.lang.Math.min;
import static java.lang.Math.max;

/**
 * @author: apanasenko aka dieu
 * Date: 03.06.2009
 * Time: 14:15:53
 */
public class AveragePerformance {
    private Collection<Performance> workers;
    private Long parsed;
    private Long parsedOne;
    private Long parsedMin = Long.MAX_VALUE;
    private Long parsedMax = Long.MIN_VALUE;
    private Long returned;
    private Long returnedMin = Long.MAX_VALUE;
    private Long returnedMax = Long.MIN_VALUE;
    private Long maxLogs = Long.MIN_VALUE;

    public AveragePerformance(Collection<Performance> workers) {
        this.workers = workers;
        findAverage();
    }

    private void findAverage() {
        Long parsedTotal = 0L;
        Long returnedTotal = 0L;
        Long logsTotal = 0L;
        for (Performance w : workers) {
            parsedTotal += w.parsed;
            parsedMin = min(parsedMin, w.parsed);
            parsedMax = max(parsedMax, w.parsed);
            returnedTotal += w.returned;
            returnedMin = min(returnedMin, w.returned);
            returnedMax = max(returnedMax, w.returned);
            logsTotal += w.logs;
            maxLogs = max(maxLogs, w.logs);
        }
        parsed = parsedTotal / workers.size();
        parsedOne = parsedTotal / logsTotal;
        returned = returnedTotal / workers.size();
    }

    public Long parsed() {
        return parsed;
    }

    public Long parsedMin() {
        return parsedMin;
    }

    public Long parsedMax() {
        return parsedMax;
    }

    public Long parsedOne() {
        return parsedOne;
    }

    public Long returned() {
        return returned;
    }

    public Long returnedMin() {
        return returnedMin;
    }

    public Long returnedMax() {
        return returnedMax;
    }

    public Long logs() {
        return maxLogs;
    }
}

