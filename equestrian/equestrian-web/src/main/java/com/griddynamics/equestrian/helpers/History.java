package com.griddynamics.equestrian.helpers;

import com.griddynamics.equestrian.model.HistoryEntity;

/**
 * @author: apanasenko aka dieu
 * Date: 18.05.2009
 * Time: 15:52:19
 */
public class History {
    private HistoryEntity historyEntity;

    public History() {
    }

    public void setHistoryEntity(HistoryEntity historyEntity) {
        this.historyEntity = historyEntity;
    }

    public void add(String date, String workers, String time, String ip, String traf) {
        historyEntity.setDate(date);
        historyEntity.setWorkers(workers);
        historyEntity.setTime(time);
        historyEntity.setIp(ip);
        historyEntity.setTraf(traf);
    }

    public String get() {
        StringBuilder result = new StringBuilder("");
        for(int i = 0; i < historyEntity.getDate().size(); i++) {
            result.append(historyEntity.getDate().get(i) + ";")
                    .append(historyEntity.getWorkers().get(i) + ";")
                    .append(historyEntity.getTime().get(i) + ";")
                    .append(historyEntity.getIp().get(i) + ";")
                    .append(historyEntity.getTraf().get(i) + ";")
                    .append(":");
        }
        if(result.toString().equals("")) {
            return "";
        } else {
            return result.deleteCharAt(result.length() - 1).toString();
        }
    }
}
