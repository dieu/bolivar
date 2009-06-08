package com.griddynamics.terracotta.helpers;

/**
 * @author apanasenko aka dieu
 *         Date: 05.06.2009
 *         Time: 13:03:18
 */
public class TimeMetr {
    private long startMeasurement;
    private long endMeasurement;
    private long resultMeasurement;
    private TypeMeasurement typeMeasurement;

    public TimeMetr(TypeMeasurement typeMeasurement) {
        this.typeMeasurement = typeMeasurement;
    }

    public long getStartMeasurement() {
        return startMeasurement;
    }

    public void setStartMeasurement(long startMeasurement) {
        this.startMeasurement = startMeasurement;
    }

    public long getEndMeasurement() {
        return endMeasurement;
    }

    public void setEndMeasurement(long endMeasurement) {
        this.endMeasurement = endMeasurement;
        this.resultMeasurement = this.endMeasurement - this.startMeasurement;
    }

    public long getResultMeasurement() {
        return resultMeasurement;
    }

    public void setResultMeasurement(long resultMeasurement) {
        this.resultMeasurement = resultMeasurement;
    }

    public TypeMeasurement getTypeMeasurement() {
        return typeMeasurement;
    }

    public void setTypeMeasurement(TypeMeasurement typeMeasurement) {
        this.typeMeasurement = typeMeasurement;
    }
}
