package com.griddynamics.terracotta.helpers;

/**
 * @author apanasenko aka dieu
 *         Date: 05.06.2009
 *         Time: 13:03:18
 */
public class TimeMetr {
    private long PutQueue;
    private long PeekQueue;
    private long countDown;
    private TypeMeasurement typeMeasurement;

    public TimeMetr(TypeMeasurement typeMeasurement) {
        this.typeMeasurement = typeMeasurement;
    }

    public long getPutQueue() {
        return PutQueue;
    }

    public void setPutQueue(long putQueue) {
        PutQueue = putQueue;
    }

    public long getPeekQueue() {
        return PeekQueue;
    }

    public void setPeekQueue(long peekQueue) {
        PeekQueue = peekQueue;
    }

    public long getCountDown() {
        return countDown;
    }

    public void setCountDown(long countDown) {
        this.countDown = countDown;
    }

    public TypeMeasurement getTypeMeasurement() {
        return typeMeasurement;
    }

    public void setTypeMeasurement(TypeMeasurement typeMeasurement) {
        this.typeMeasurement = typeMeasurement;
    }
}
